package com.vecsight.oj

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.vecsight.oj.model.IpLimitModel
import com.vecsight.oj.model.JudgeQueueModel
import com.vecsight.oj.model.ProblemModel
import com.vecsight.oj.model.RecordModel
import com.vecsight.oj.pojo.Problem
import com.vecsight.oj.pojo.Record
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class TestContext : Context {
    override fun getJedis(): Jedis {
        throw UnsupportedOperationException("not implemented")
    }

    val _ipLimitModel = object : IpLimitModel {
        override fun plus(ip: String): Int {
            return 0
        }

    }

    override fun getIpLimitModel(): IpLimitModel {
        return _ipLimitModel
    }

    val _problemModel = object : ProblemModel {
        val list = LinkedList<Problem>()

        init {
            list.add(Problem(id = "test", title = "test", description = "test", showInput = true, showCompilerError = true, showExpect = true, showOutput = true))
        }

        override fun getById(id: String): Problem? {
            return list.firstOrNull { problem -> if (problem.id == id) true else false }
        }

        override fun update(meta: Problem): Problem? {
            if (meta.id == null) {
                val problem = meta.copy(id = UUID.randomUUID().toString())
                list.add(problem)
                return problem
            }
            val problem = list.firstOrNull { problem -> if (problem.id == meta.id) true else false }
            if (problem == null) {
                return null
            }
            list.removeAll { problem -> if (problem.id == meta.id) true else false }
            list.add(meta)
            return meta
        }

    }

    override fun getProblemModel(): ProblemModel {
        return _problemModel
    }

    val _recordModel = object : RecordModel {
        val list = LinkedList<Record>()

        override fun getById(id: String): Record? {
            return list.firstOrNull { record -> if (record.id == id) true else false }
        }

        override fun update(meta: Record): Record? {
            if (meta.id == null) {
                val record = meta.copy(id = UUID.randomUUID().toString())
                list.add(record)
                return record
            }
            val record = list.firstOrNull { record -> if (record.id == meta.id) true else false }
            if (record == null) {
                return null
            }
            list.removeAll { record -> if (record.id == record.id) true else false }
            list.add(meta)
            return meta
        }

    }

    override fun getRecordModel(): RecordModel {
        return _recordModel
    }

    val _judgeQueueModel = object : JudgeQueueModel {
        val logger = LoggerFactory.getLogger("Judge Queue")

        val queue = LinkedBlockingQueue<String>()

        override fun add(recordId: String) {
            queue.add(recordId)
            logger.info("add: $recordId")
            logger.info("size: ${queue.size}")
        }

        override fun remove(): String? {
            return try {
                val r = queue.remove()
                logger.info("remove: $r")
                logger.info("size: ${queue.size}")
                r
            } catch (e: NoSuchElementException) {
                logger.info("remove failed")
                logger.info("size: ${queue.size}")
                null
            }
        }

        override fun indexOf(recordId: String): Int {
            return queue.indexOf(recordId)
        }

    }

    override fun getJudgeQueueModel(): JudgeQueueModel {
        return _judgeQueueModel
    }

    override fun getDockerClient(): DockerClient {
        val config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build()
        return DockerClientBuilder.getInstance(config).build()
    }

    val _objectMapper = {
        val o = ObjectMapper()
        o.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
        o
    }()

    override fun getObjectMapper(): ObjectMapper {
        return _objectMapper
    }
}
package com.vecsight.oj

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.vecsight.oj.model.JudgeQueueModel
import com.vecsight.oj.model.ProblemModel
import com.vecsight.oj.model.RecordModel
import com.vecsight.oj.pojo.Problem
import com.vecsight.oj.pojo.Record
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class TestContext : Context {
    val _problemModel = object : ProblemModel {
        val list = LinkedList<Problem>()

        init {
            list.add(Problem(id = "test", title = "test", description = "test", showInput = true, showCompilerError = true, showExpect = true, showOutput = true))
        }

        override fun getById(id: String): Problem? {
            return list.firstOrNull { problem -> if (problem.id == id) true else false }
        }

        override fun update(template: Problem): Problem? {
            if (template.id == null) {
                val problem = template.copy(id = UUID.randomUUID().toString())
                list.add(problem)
                return problem
            }
            val problem = list.firstOrNull { problem -> if (problem.id == template.id) true else false }
            if (problem == null) {
                return null
            }
            list.removeAll { problem -> if (problem.id == template.id) true else false }
            list.add(template)
            return template
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

        override fun update(template: Record): Record? {
            if (template.id == null) {
                val record = template.copy(id = UUID.randomUUID().toString())
                list.add(record)
                return record
            }
            val record = list.firstOrNull { record -> if (record.id == template.id) true else false }
            if (record == null) {
                return null
            }
            list.removeAll { record -> if (record.id == record.id) true else false }
            list.add(template)
            return template
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

        override fun indexOf(recordId: String): Int? {
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

    override fun getObjectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}
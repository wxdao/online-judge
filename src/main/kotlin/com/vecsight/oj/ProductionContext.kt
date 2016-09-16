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
import com.vecsight.oj.model.impl.RedisIpLimitModel
import com.vecsight.oj.model.impl.RedisJudgeQueueModel
import com.vecsight.oj.model.impl.RedisProblemModel
import com.vecsight.oj.model.impl.RedisRecordModel
import redis.clients.jedis.Jedis

class ProductionContext : Context {
    val _objectMapper = {
        val o = ObjectMapper()
        o.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
        o
    }()

    override fun getObjectMapper(): ObjectMapper {
        return _objectMapper
    }

    val _dockerClient = {
        val config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build()
        DockerClientBuilder.getInstance(config).build()
    }()

    override fun getDockerClient(): DockerClient {
        return _dockerClient
    }

    val _jedis = Jedis("localhost")

    override fun getJedis(): Jedis {
        return _jedis
    }

    val _ipLimitModel by lazy {
        RedisIpLimitModel()
    }

    override fun getIpLimitModel(): IpLimitModel {
        return _ipLimitModel
    }

    val _problemModel by lazy {
        RedisProblemModel()
    }

    override fun getProblemModel(): ProblemModel {
        return _problemModel
    }

    val _recordModel by lazy {
        RedisRecordModel()
    }

    override fun getRecordModel(): RecordModel {
        return _recordModel
    }

    val _judgeQueueModel by lazy {
        RedisJudgeQueueModel()
    }

    override fun getJudgeQueueModel(): JudgeQueueModel {
        return _judgeQueueModel
    }
}
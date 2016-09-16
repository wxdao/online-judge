package com.vecsight.oj

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.vecsight.oj.model.JudgeQueueModel
import com.vecsight.oj.model.ProblemModel
import com.vecsight.oj.model.RecordModel

class TestContext : Context {
    override fun getProblemModel(): ProblemModel {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRecordModel(): RecordModel {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getJudgeQueueModel(): JudgeQueueModel {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
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
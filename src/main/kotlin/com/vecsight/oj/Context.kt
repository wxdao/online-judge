package com.vecsight.oj

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dockerjava.api.DockerClient
import com.vecsight.oj.model.IpLimitModel
import com.vecsight.oj.model.JudgeQueueModel
import com.vecsight.oj.model.ProblemModel
import com.vecsight.oj.model.RecordModel
import redis.clients.jedis.Jedis

interface Context {
    fun getObjectMapper(): ObjectMapper

    fun getDockerClient(): DockerClient

    fun getJedis(): Jedis

    fun getIpLimitModel(): IpLimitModel

    fun getProblemModel(): ProblemModel

    fun getRecordModel(): RecordModel

    fun getJudgeQueueModel(): JudgeQueueModel
}
package com.vecsight.oj.model.impl

import com.vecsight.oj.Application
import com.vecsight.oj.model.JudgeQueueModel

class RedisJudgeQueueModel : JudgeQueueModel {
    val redis = Application.context!!.getJedis()

    val key = "OJ_JUDGE_QUEUE"

    override fun add(recordId: String) {
        redis.rpush(key, recordId)
    }

    override fun remove(): String? {
        return redis.lpop(key)
    }

    override fun indexOf(recordId: String): Int {
        val v = redis.lrange(key, 0, -1)
        return v.indexOf(recordId)
    }
}
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
        val id = redis.lpop(key) ?: return null
        redis.setex(key + "-JUDGING:" + id, 30, "judging")
        return id
    }

    override fun indexOf(recordId: String): Int {
        val v = redis.lrange(key, 0, -1)
        val idx = v.indexOf(recordId)
        if (idx == -1) {
            if (redis.exists(key + "-JUDGING:" + recordId)) {
                return -2
            } else {
                return -1
            }
        } else {
            return idx
        }
    }
}
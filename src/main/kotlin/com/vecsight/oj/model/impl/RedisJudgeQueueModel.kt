package com.vecsight.oj.model.impl

import com.vecsight.oj.Application
import com.vecsight.oj.model.JudgeQueueModel
import org.slf4j.LoggerFactory

class RedisJudgeQueueModel : JudgeQueueModel {
    val redisPool = Application.context!!.getJedisPool()

    val key = "OJ_JUDGE_QUEUE"

    val logger = LoggerFactory.getLogger("Judge Queue Model")

    override fun add(recordId: String) {
        redisPool.resource.use { redis ->
            redis.rpush(key, recordId)
        }
    }

    override fun remove(): String? {
        try {
            redisPool.resource.use { redis ->
                val id = redis.lpop(key) ?: return null
                redis.setex(key + "-JUDGING:" + id, 30, "judging")
                return id
            }
        } catch (e: Exception) {
            logger.error(e.message)
            return null
        }
    }

    override fun indexOf(recordId: String): Int {
        redisPool.resource.use { redis ->
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
}
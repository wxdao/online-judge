package com.vecsight.oj.model.impl

import com.vecsight.oj.Application
import com.vecsight.oj.model.IpLimitModel

class RedisIpLimitModel : IpLimitModel {
    val redisPool = Application.context!!.getJedisPool()

    val prefix = "OJ_IP_LIMIT:"

    override fun plus(ip: String): Int {
        redisPool.resource.use { redis ->
            val count = redis.incr(prefix + ip)
            if (redis.ttl(prefix + ip) < 0) {
                redis.expire(prefix + ip, 60)
            }
            return count.toInt()
        }
    }
}
package com.vecsight.oj.model.impl

import com.vecsight.oj.Application
import com.vecsight.oj.model.RecordModel
import com.vecsight.oj.pojo.Record
import java.util.*

class RedisRecordModel : RecordModel {
    val redisPool = Application.context!!.getJedisPool()

    val mapper = Application.context!!.getObjectMapper()

    val key = "OJ_RECORDS"

    override fun getById(id: String): Record? {
        redisPool.resource.use { redis ->
            val r = redis.hget(key, id) ?: return null
            try {
                return mapper.readValue(r, Record::class.java)
            } catch (e: Exception) {
                redis.hdel(key, id)
                return null
            }
        }
    }

    override fun update(meta: Record): Record? {
        redisPool.resource.use { redis ->
            if (meta.id == null) {
                val record = meta.copy(id = UUID.randomUUID().toString().replace("-", ""))
                redis.hset(key, record.id, mapper.writeValueAsString(record))
                return record
            }
            redis.hset(key, meta.id, mapper.writeValueAsString(meta))
            return meta
        }
    }
}
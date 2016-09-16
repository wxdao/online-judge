package com.vecsight.oj.model.impl

import com.vecsight.oj.Application
import com.vecsight.oj.model.RecordModel
import com.vecsight.oj.pojo.Record
import java.util.*

class RedisRecordModel : RecordModel {
    val redis = Application.context!!.getJedis()

    val mapper = Application.context!!.getObjectMapper()

    val key = "OJ_RECORDS"

    override fun getById(id: String): Record? {
        val r = redis.hget(key, id) ?: return null
        try {
            return mapper.readValue(r, Record::class.java)
        } catch (e: Exception) {
            redis.hdel(key, id)
            return null
        }

    }

    override fun update(template: Record): Record? {
        if (template.id != null) {
            redis.hget(key, template.id) ?: return null
            redis.hset(key, template.id, mapper.writeValueAsString(template))
            return template
        }
        val record = template.copy(id = UUID.randomUUID().toString().replace("-", ""))
        redis.hset(key, record.id, mapper.writeValueAsString(record))
        return record
    }
}
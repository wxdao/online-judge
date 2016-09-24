package com.vecsight.oj.model.impl

import com.vecsight.oj.Application
import com.vecsight.oj.model.ProblemModel
import com.vecsight.oj.pojo.Problem
import java.util.*

class RedisProblemModel : ProblemModel {
    val redis = Application.context!!.getJedis()

    val mapper = Application.context!!.getObjectMapper()

    val key = "OJ_PROBLEMS"

    override fun getById(id: String): Problem? {
        val r = redis.hget(key, id) ?: return null
        try {
            return mapper.readValue(r, Problem::class.java)
        } catch (e: Exception) {
            redis.hdel(key, id)
            return null
        }

    }

    override fun update(meta: Problem): Problem? {
        if (meta.id == null) {
            val problem = meta.copy(id = UUID.randomUUID().toString().replace("-", ""))
            redis.hset(key, problem.id, mapper.writeValueAsString(problem))
            return problem
        }
        redis.hset(key, meta.id, mapper.writeValueAsString(meta))
        return meta
    }
}
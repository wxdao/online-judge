package com.vecsight.oj.config

import com.vecsight.oj.Application
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.*

object ConfigHelper {
    val cache = HashMap<String, Config>()
    fun <T : Config> retrieve(clazz: Class<T>, uri: URI = URI.create("file:///etc/oj/${clazz.canonicalName}.json"), returnNullWhenFailed: Boolean = false): T? {
        val logger = LoggerFactory.getLogger("ConfigHelper")
        val mapper = Application.context!!.getObjectMapper()
        val cached = cache[clazz.canonicalName]
        if (cached != null) {
            @Suppress("UNCHECKED_CAST")
            return cached as T
        }
        var res: T? = null
        try {
            val stream = uri.toURL().openStream()
            res = mapper.readValue(stream, clazz)
        } catch (e: Exception) {
            logger.warn(e.message)
        }
        if (res == null) {
            if (returnNullWhenFailed) {
                return null
            } else {
                res = clazz.newInstance()
                cache[clazz.canonicalName] = res
                return clazz.newInstance()
            }
        } else {
            cache[clazz.canonicalName] = res
            return res
        }
    }
}
package com.vecsight.oj.config

import com.vecsight.oj.Application
import org.slf4j.LoggerFactory
import java.net.URI

object ConfigHelper {
    fun <T : Config> retrieve(clazz: Class<T>, uri: URI = URI.create("file:///etc/oj/${clazz.canonicalName}.json"), returnNullWhenFailed: Boolean = false): T? {
        val logger = LoggerFactory.getLogger("ConfigHelper")
        val mapper = Application.context!!.getObjectMapper()
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
                return clazz.newInstance()
            }
        } else {
            return res
        }
    }
}
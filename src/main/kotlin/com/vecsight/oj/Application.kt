package com.vecsight.oj

import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.ResourceConfig

class Application : ResourceConfig() {
    init {
        packages("com.vecsight.oj.resource")
        register(LoggingFeature::class.java)
        register(MultiPartFeature::class.java)
    }

    companion object {
        var context: Context? = null
    }
}
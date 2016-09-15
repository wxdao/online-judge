package com.vecsight.oj

import com.vecsight.oj.config.ConfigHelper
import com.vecsight.oj.config.MainConfig
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory
import org.slf4j.LoggerFactory
import javax.ws.rs.core.UriBuilder

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Entry")
    logger.info("Using: {}", "JDK HTTP Server")
    Application.context = TestContext()
    val mc = ConfigHelper.retrieve(MainConfig::class.java)
    logger.info("Opening server on ${mc!!.host}:${mc.port}")
    val baseUri = UriBuilder.fromUri("http://${mc.host}/").port(mc.port).build()
    val app = Application()
    JdkHttpServerFactory.createHttpServer(baseUri, app)
    Thread.currentThread().join()
}
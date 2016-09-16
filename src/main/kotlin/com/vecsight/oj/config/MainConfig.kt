package com.vecsight.oj.config

class MainConfig : Config {
    val host: String = System.getenv("OJ_HOST") ?: "localhost"
    val port: Int = {
        val envPort = System.getenv("OJ_PORT")
        if (envPort != null) {
            envPort.toInt()
        } else {
            8080
        }
    }()
    val packRoot: String = System.getenv("OJ_PACK_ROOT") ?: "/var/packs/"
}
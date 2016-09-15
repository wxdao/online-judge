package com.vecsight.oj.config

class MainConfig : Config {
    var host: String = System.getenv("OJ_HOST") ?: "localhost"
    var port: Int = {
        val envPort = System.getenv("OJ_PORT")
        if (envPort != null) {
            envPort.toInt()
        } else {
            8080
        }
    }()
}
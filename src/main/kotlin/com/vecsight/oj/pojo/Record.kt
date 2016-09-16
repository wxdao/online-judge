package com.vecsight.oj.pojo

data class Record(
        val id: String? = null,
        val problemId: String? = null,
        val timestamp: String? = null,
        val source: String? = null,
        val result: String? = null,
        val message: String? = null,
        val compilerError: String? = null,
        val input: String? = null,
        val expect: String? = null,
        val output: String? = null
)
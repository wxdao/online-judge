package com.vecsight.oj.pojo

data class Problem(
        val id: String? = null,
        val title: String? = null,
        val description: String? = null,
        val template: String? = null,
        val showInput: Boolean = false,
        val showCompilerError: Boolean = false,
        val showExpect: Boolean = false,
        val showOutput: Boolean = false
)
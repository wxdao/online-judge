package com.vecsight.oj.model

interface JudgeQueueModel {
    fun add(recordId: String): String

    fun remove(): String?

    fun indexOf(recordId: String): Int?
}
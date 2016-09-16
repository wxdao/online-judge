package com.vecsight.oj.model

interface JudgeQueueModel {
    fun add(recordId: String): String

    fun remove(): String?
}
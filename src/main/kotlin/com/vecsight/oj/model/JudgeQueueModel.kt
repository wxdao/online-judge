package com.vecsight.oj.model

interface JudgeQueueModel {
    fun add(recordId: String)

    fun remove(): String?

    fun blockingRemove(): String?

    fun indexOf(recordId: String): Int
}
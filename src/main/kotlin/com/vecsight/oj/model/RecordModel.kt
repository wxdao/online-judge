package com.vecsight.oj.model

import com.vecsight.oj.pojo.Record

interface RecordModel {
    fun getById(id: String): Record?

    fun new(template: Record): Record?
}
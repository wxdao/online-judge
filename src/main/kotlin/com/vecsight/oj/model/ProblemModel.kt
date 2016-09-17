package com.vecsight.oj.model

import com.vecsight.oj.pojo.Problem

interface ProblemModel {
    fun getById(id: String): Problem?

    fun update(meta: Problem): Problem?
}
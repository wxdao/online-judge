package com.vecsight.oj.model

import com.vecsight.oj.pojo.Problem

interface ProblemModel {
    fun getById(id: String): Problem?

    fun new(template: Problem): Problem?
}
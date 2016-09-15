package com.vecsight.oj

import com.fasterxml.jackson.databind.ObjectMapper

class TestContext : Context {
    override fun getObjectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}
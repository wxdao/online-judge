package com.vecsight.oj

import com.fasterxml.jackson.databind.ObjectMapper

interface Context {
    fun getObjectMapper(): ObjectMapper

}
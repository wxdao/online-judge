package com.vecsight.oj.resource

data class ErrorEntity(val err: Int, val errStr: String) : Entity {
    companion object {
        val INTERNAL = ErrorEntity(-1, "internal error")
        val INVALID_REQUEST = ErrorEntity(1, "invalid request")
        val NO_SUCH_PROBLEM = ErrorEntity(10001, "no such problem")
        val CANNOT_CREATE_RECORD = ErrorEntity(20001, "cannot create record")
        val NO_SUCH_RECORD = ErrorEntity(30001, "no such record")
        val IP_LIMIT = ErrorEntity(40001, "ip limit exceeded")
    }
}
package com.vecsight.oj.resource

import com.vecsight.oj.Application
import com.vecsight.oj.pojo.Record
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("judge")
class Judge {
    data class ProblemResponse(val title: String, val description: String)

    @Path("problem/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun problem(@PathParam("id") id: String?): ProblemResponse {
        if (id == null) {
            throw WebException(ErrorEntity.INVALID_REQUEST, 400)
        }
        val problem = Application.context!!.getProblemModel().getById(id) ?: throw WebException(ErrorEntity.NO_SUCH_PROBLEM, 400)
        return ProblemResponse(problem.title ?: "", problem.description ?: "")
    }

    data class SubmitRequest(val problemId: String? = null, val source: String? = null)
    data class SubmitResponse(val recordId: String)

    @Path("submit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun submit(request: SubmitRequest?): SubmitResponse {
        if (request == null || request.problemId == null || request.source == null) {
            throw WebException(ErrorEntity.INVALID_REQUEST, 400)
        }
        val record = Record(problemId = request.problemId, timestamp = (System.currentTimeMillis() / 1000L).toString(), source = request.source)
        val updatedRecord = Application.context!!.getRecordModel().update(record)
        if (updatedRecord == null) {
            throw WebException(ErrorEntity.CANNOT_CREATE_RECORD, 502)
        }
        Application.context!!.getJudgeQueueModel().add(updatedRecord.id!!)
        return SubmitResponse(updatedRecord.id)
    }

    data class RecordResponse(
            val status: String,
            val timestamp: String,
            val queue: Int? = null,
            val result: String? = null,
            val message: String? = null,
            val compilerError: String? = null,
            val input: String? = null,
            val expect: String? = null,
            val output: String? = null
    )

    @Path("record/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun record(@PathParam("id") id: String?): RecordResponse {
        if (id == null) {
            throw WebException(ErrorEntity.INVALID_REQUEST, 400)
        }
        val record = Application.context!!.getRecordModel().getById(id)
        if (record == null) {
            throw WebException(ErrorEntity.NO_SUCH_RECORD, 400)
        }
        val problem = Application.context!!.getProblemModel().getById(record.problemId!!) ?: throw WebException(ErrorEntity.INTERNAL, 502)
        if (record.result == null) {
            var index = Application.context!!.getJudgeQueueModel().indexOf(record.id!!)
            if (index == -1) {
                Application.context!!.getJudgeQueueModel().add(record.id)
                index = Application.context!!.getJudgeQueueModel().indexOf(record.id)
            }
            return RecordResponse("pending", record.timestamp!!, queue = index)
        }
        return RecordResponse(
                "judged",
                record.timestamp!!,
                result = record.result,
                message = record.message,
                compilerError = if (problem.showCompilerError) record.compilerError else null,
                input = if (problem.showInput) record.input else null,
                expect = if (problem.showExpect) record.expect else null,
                output = if (problem.showOutput) record.output else null
        )
    }
}
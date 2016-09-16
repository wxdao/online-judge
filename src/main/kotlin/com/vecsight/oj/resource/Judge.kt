package com.vecsight.oj.resource

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("judge")
class Judge {
    @Path("question/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun question() {

    }

    @Path("submit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun submit() {

    }

    @Path("record/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun progress() {

    }
}
package com.vecsight.oj.resource

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

@Provider
@AuthRequired
class AuthRequiredRequestFilter : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {
        val entry = requestContext.getHeaderString("X-Authentication-Code")
        if (entry != "I AM Quanxian Dog!!!") {
            requestContext.abortWith(Response.status(402).entity(ErrorEntity.AUTH).type(MediaType.APPLICATION_JSON).build())
        }
    }
}
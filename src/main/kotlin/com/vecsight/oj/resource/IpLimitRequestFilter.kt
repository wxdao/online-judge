package com.vecsight.oj.resource

import com.vecsight.oj.Application
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

@Provider
@IpLimit
class IpLimitRequestFilter : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {
        val remoteIp = requestContext.getHeaderString("X-Forwarded-For")?.split(",")?.last() ?: return
        val model = Application.context!!.getIpLimitModel()
        val count = model.plus(remoteIp)
        if (count > 5) {
            requestContext.abortWith(Response.status(402).entity(ErrorEntity.IP_LIMIT).type(MediaType.APPLICATION_JSON).build())
        }
    }
}
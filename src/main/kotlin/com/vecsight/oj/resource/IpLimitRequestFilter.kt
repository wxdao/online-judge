package com.vecsight.oj.resource

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.ext.Provider

@Provider
@IpLimit
class IpLimitRequestFilter : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {
        val remoteIp = requestContext.getHeaderString("X-Forwarded-For")?.split(",")?.last() ?: return

    }
}
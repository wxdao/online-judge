package com.vecsight.oj.resource

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class WebException(entity: Entity, statusCode: Int) : WebApplicationException(Response.status(statusCode).type(MediaType.APPLICATION_JSON_TYPE).entity(entity).build()) {
}
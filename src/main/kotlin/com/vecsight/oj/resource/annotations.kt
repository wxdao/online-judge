package com.vecsight.oj.resource

import javax.ws.rs.NameBinding

@NameBinding
@Retention(AnnotationRetention.RUNTIME)
annotation class IpLimit

@NameBinding
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthRequired
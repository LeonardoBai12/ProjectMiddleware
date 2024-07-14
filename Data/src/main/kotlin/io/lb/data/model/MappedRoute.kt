package io.lb.data.model

import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader
import io.lb.data.util.MiddlewareHttpMethods

data class MappedRoute(
    val mappedApi: MappedApi,
    val method: MiddlewareHttpMethods,
    val authHeader: HttpAuthHeader? = null,
    val headers: Map<HttpHeaders, String> = mapOf(),
    val path: String,
    val query: Map<String, String>,
    val body: String?
)

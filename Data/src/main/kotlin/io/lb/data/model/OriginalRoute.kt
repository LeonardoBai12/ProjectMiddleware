package io.lb.data.model

import io.lb.data.util.MIddlewareAuthHeader
import io.lb.data.util.MiddlewareHttpMethods

data class OriginalRoute(
    val mappedApi: MappedApi,
    val method: MiddlewareHttpMethods,
    val authHeader: MIddlewareAuthHeader? = null,
    val headers: Map<String, String> = mapOf(),
    val path: String,
    val queries: Map<String, String>,
    val body: String?
)

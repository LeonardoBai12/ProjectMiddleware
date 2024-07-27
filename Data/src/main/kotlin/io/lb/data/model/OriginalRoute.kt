package io.lb.data.model

import io.lb.data.util.MiddlewareAuthHeader
import io.lb.data.util.MiddlewareHttpMethods

data class OriginalRoute(
    val path: String,
    val originalApi: OriginalApi,
    val method: MiddlewareHttpMethods,
    val authHeader: MiddlewareAuthHeader? = null,
    val headers: Map<String, String> = mapOf(),
    val queries: Map<String, String>,
    val body: String?
)

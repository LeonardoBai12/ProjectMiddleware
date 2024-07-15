package io.lb.database.model

import io.lb.data.model.OriginalApi
import io.lb.data.util.MiddlewareAuthHeader
import io.lb.data.util.MiddlewareHttpMethods

data class OriginalRouteEntity(
    val path: String,
    val originalApi: OriginalApi,
    val method: MiddlewareHttpMethods,
    val authHeader: MiddlewareAuthHeader? = null,
    val headers: Map<String, String> = mapOf(),
    val queries: Map<String, String>,
    val body: String?
)

package io.lb.data.model

import io.lb.data.util.MiddlewareAuthHeader
import io.lb.data.util.MiddlewareHttpMethods
import java.util.UUID

data class MappedRoute(
    val uuid: UUID = UUID.randomUUID(),
    val path: String,
    val mappedApi: MappedApi,
    val originalRoute: OriginalRoute,
    val method: MiddlewareHttpMethods,
    val authHeader: MiddlewareAuthHeader? = null,
    val headers: Map<String, String> = mapOf(),
    val query: Map<String, String>?,
    val body: String?
)

package io.lb.database.model

import io.ktor.http.HttpHeaders
import io.lb.data.model.OriginalRoute
import io.lb.data.util.MiddlewareAuthHeader
import io.lb.data.util.MiddlewareHttpMethods
import java.util.UUID

data class MappedRouteEntity(
    val uuid: UUID,
    val path: String,
    val originalRoute: OriginalRoute,
    val method: MiddlewareHttpMethods,
    val authHeader: MiddlewareAuthHeader? = null,
    val headers: Map<HttpHeaders, String> = mapOf(),
    val query: Map<String, String>?,
    val body: String?
)

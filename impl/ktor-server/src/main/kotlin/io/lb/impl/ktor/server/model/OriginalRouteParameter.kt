package io.lb.impl.ktor.server.model

import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareAuthHeader
import io.lb.common.data.request.MiddlewareHttpMethods
import kotlinx.serialization.Serializable

@Serializable
data class OriginalRouteParameter(
    val path: String,
    val originalApi: OriginalApiParameter,
    val method: MiddlewareHttpMethods,
    val authHeader: Map<String, String>? = null,
    val headers: Map<String, String> = mapOf(),
    val body: String? = null
) {
    fun toOriginalRoute() = OriginalRoute(
        path = path,
        originalApi = originalApi.toOriginalApi(),
        method = method,
        authHeader = MiddlewareAuthHeader.fromMap(authHeader),
        headers = headers,
        body = body
    )
}

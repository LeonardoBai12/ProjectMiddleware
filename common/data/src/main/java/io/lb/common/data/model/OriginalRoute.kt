package io.lb.common.data.model

import io.lb.common.data.request.MiddlewareAuthHeader
import io.lb.common.data.request.MiddlewareHttpMethods
import kotlinx.serialization.Serializable

/**
 * Data class representing an original route.
 *
 * @property path The path of the original route.
 * @property originalApi The original API.
 * @property method The HTTP method of the original route.
 * @property authHeader The authentication header of the original route.
 * @property queries The queries of the original route.
 * @property headers The headers of the original route.
 * @property body The body of the original route.
 */
@Serializable
data class OriginalRoute(
    val path: String,
    val originalApi: OriginalApi,
    val method: MiddlewareHttpMethods,
    val authHeader: MiddlewareAuthHeader? = null,
    val queries: Map<String, String> = mapOf(),
    val headers: Map<String, String> = mapOf(),
    val body: String? = null
)

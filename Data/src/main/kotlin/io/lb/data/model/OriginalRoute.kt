package io.lb.data.model

import io.lb.data.util.MiddlewareAuthHeader
import io.lb.data.util.MiddlewareHttpMethods

/**
 * Data class representing an original route.
 *
 * @property path The path of the original route.
 * @property originalApi The original API.
 * @property method The HTTP method of the original route.
 * @property authHeader The authentication header of the original route.
 * @property headers The headers of the original route.
 * @property body The body of the original route.
 */
data class OriginalRoute(
    val path: String,
    val originalApi: OriginalApi,
    val method: MiddlewareHttpMethods,
    val authHeader: MiddlewareAuthHeader? = null,
    val headers: Map<String, String> = mapOf(),
    val body: String?
)

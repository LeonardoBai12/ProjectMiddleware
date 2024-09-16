package io.lb.impl.ktor.server.model

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.request.MiddlewareHttpMethods
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Data class representing a mapped route.
 *
 * @property path The path of the mapped route.
 * @property originalRoute The original route.
 * @property method The HTTP method of the mapped route.
 * @property preConfiguredQueries The pre-configured queries of the mapped route.
 * @property preConfiguredHeaders The pre-configured headers of the mapped route.
 * @property preConfiguredBody The pre-configured body of the mapped route.
 * @property rulesAsString The mapping rules as a string.
 */
@Serializable
internal data class MappedRouteParameter(
    val path: String,
    val originalRoute: OriginalRouteParameter,
    val method: MiddlewareHttpMethods,
    val preConfiguredQueries: Map<String, String> = mapOf(),
    val preConfiguredHeaders: Map<String, String> = originalRoute.headers,
    val preConfiguredBody: JsonObject? = originalRoute.body,
    val rulesAsString: JsonObject?
) {
    fun toMappedRoute() = MappedRoute(
        path = path,
        originalRoute = originalRoute.toOriginalRoute(),
        mappedApi = MappedApi(originalApi = originalRoute.originalApi.toOriginalApi()),
        method = method,
        preConfiguredQueries = preConfiguredQueries,
        preConfiguredHeaders = preConfiguredHeaders,
        preConfiguredBody = preConfiguredBody,
        rulesAsString = rulesAsString.toString()
    )
}

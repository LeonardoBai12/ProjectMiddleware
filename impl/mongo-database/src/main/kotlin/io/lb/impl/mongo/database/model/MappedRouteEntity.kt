package io.lb.impl.mongo.database.model

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareHttpMethods

/**
 * Entity for a mapped route.
 *
 * @property uuid The UUID of the route.
 * @property path The path of the route.
 * @property originalRoute The original route.
 * @property method The HTTP method.
 * @property preConfiguredQueries Pre-configured queries.
 * @property preConfiguredHeaders Pre-configured headers.
 * @property preConfiguredBody Pre-configured body.
 * @property rulesAsString The mapping rules as a string.
 */
internal data class MappedRouteEntity(
    val uuid: String,
    val path: String,
    val originalRoute: OriginalRoute,
    val method: MiddlewareHttpMethods,
    val preConfiguredQueries: Map<String, String>,
    val preConfiguredHeaders: Map<String, String>,
    val preConfiguredBody: String?,
    val rulesAsString: String?,
) {
    /**
     * Converts the entity to a mapped route.
     *
     * @param mappedApi The mapped API.
     * @return The mapped route.
     */
    internal fun toRoute(mappedApi: MappedApi): MappedRoute {
        return MappedRoute(
            uuid = this.uuid,
            path = this.path,
            mappedApi = mappedApi,
            originalRoute = this.originalRoute,
            method = this.method,
            preConfiguredQueries = this.preConfiguredQueries,
            preConfiguredHeaders = this.preConfiguredHeaders,
            preConfiguredBody = this.preConfiguredBody,
            rulesAsString = this.rulesAsString,
        )
    }
}

/**
 * Converts a mapped route to an entity.
 *
 * @return The entity.
 */
internal fun MappedRoute.toEntity(): MappedRouteEntity {
    return MappedRouteEntity(
        uuid = uuid,
        path = "/v1/${uuid}/${path}",
        originalRoute = originalRoute,
        method = method,
        preConfiguredQueries = this.preConfiguredQueries,
        preConfiguredHeaders = this.preConfiguredHeaders,
        preConfiguredBody = this.preConfiguredBody,
        rulesAsString = rulesAsString,
    )
}

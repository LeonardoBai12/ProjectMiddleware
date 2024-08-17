package io.lb.impl.mongo.database.model

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.common.data.model.OriginalRoute
import java.util.UUID

/**
 * Entity for a mapped route.
 *
 * @property uuid The UUID of the route.
 * @property path The path of the route.
 * @property originalRoute The original route.
 * @property method The HTTP method.
 * @property body The body.
 */
data class MappedRouteEntity(
    val uuid: UUID,
    val path: String,
    val originalRoute: OriginalRoute,
    val method: MiddlewareHttpMethods,
    val body: String?
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
            body = this.body,
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
        path = path,
        originalRoute = originalRoute,
        method = method,
        body = body,
    )
}

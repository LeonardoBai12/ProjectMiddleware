package io.lb.common.data.service

import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.shared.error.MiddlewareException

/**
 * Service interface for interacting with the server.
 */
interface ServerService {
    /**
     * Creates a mapped route.
     *
     * @param mappedRoute The mapped route to create.
     * @param onRequest The request handler for the original route.
     */
    @Throws(MiddlewareException::class)
    fun createMappedRoute(
        mappedRoute: MappedRoute,
        onRequest: (OriginalRoute, Map<String, String>) -> OriginalResponse
    )

    /**
     * Creates a list of mapped routes.
     *
     * @param mappedRoutes The mapped routes to create.
     * @param onRequest The request handler for the original route.
     */
    @Throws(MiddlewareException::class)
    fun createMappedRoutes(
        mappedRoutes: List<MappedRoute>,
        onRequest: (OriginalRoute, Map<String, String>) -> OriginalResponse
    )
}

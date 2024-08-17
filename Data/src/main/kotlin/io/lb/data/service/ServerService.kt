package io.lb.data.service

import io.lb.common.error.MiddlewareException
import io.lb.data.model.MappedRoute
import io.lb.data.model.OriginalResponse
import io.lb.data.model.OriginalRoute

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
        onRequest: (OriginalRoute) -> OriginalResponse
    )

    /**
     * Creates a list of mapped routes.
     *
     * @param mappedRoutes The mapped routes to create.
     * @param onCompletion The request handler for the original route.
     */
    @Throws(MiddlewareException::class)
    fun createMappedRoutes(
        mappedRoutes: List<MappedRoute>,
        onCompletion: (OriginalRoute) -> OriginalResponse
    )
}

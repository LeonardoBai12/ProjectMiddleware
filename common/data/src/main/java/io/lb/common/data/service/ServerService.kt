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
     * Starts the configuration route.
     *
     * @param onReceive The handler for the configuration route. It receives the mapped route and returns
     * the URL for the mapped route.
     */
    fun startGenericMappingRoute(onReceive: (MappedRoute) -> String)

    /**
     * Creates a mapped route.
     *
     * @param mappedRoute The mapped route to create.
     * @param onRequest The request handler for the original route, with the mapped route,
     * queries, headers, and body.
     */
    @Throws(MiddlewareException::class)
    fun createMappedRoute(
        mappedRoute: MappedRoute,
        onRequest: (OriginalRoute, Map<String, String>, Map<String, String>, String?) -> OriginalResponse
    )

    /**
     * Creates mapped routes based on a list.
     *
     * @param mappedRoutes The mapped routes to create.
     * @param onEachRequest The request handler for the original route, with the mapped route,
     * queries, headers, and body.
     */
    @Throws(MiddlewareException::class)
    fun createMappedRoutes(
        mappedRoutes: List<MappedRoute>,
        onEachRequest: (OriginalRoute, Map<String, String>, Map<String, String>, String?) -> OriginalResponse
    )
}

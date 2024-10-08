package io.lb.common.data.service

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.shared.error.MiddlewareException

/**
 * Service interface for interacting with the server.
 */
interface ServerService {
    /**
     * Starts the generic mapping route.
     * @param onReceive The handler for the generic mapping route. It receives the mapped route and returns the URL.
     */
    fun startGenericMappingRoute(onReceive: suspend (MappedRoute) -> String)

    /**
     * Starts the query all routes route.
     * @param onReceive The handler for the query all routes route.
     * It receives the URL and returns the list of mapped routes.
     */
    fun startQueryAllRoutesRoute(onReceive: suspend (String) -> List<MappedRoute>)

    /**
     * Starts the preview route.
     * @param onReceive The handler for the preview route. It receives the mapping rules and returns the preview.
     */
    fun startPreviewRoute(onReceive: (String, String) -> String)

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
        onRequest: suspend (MappedRoute) -> MappedResponse
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
        onEachRequest: suspend (MappedRoute) -> MappedResponse
    )

    /**
     * Stops the server.
     */
    fun stopServer()
}

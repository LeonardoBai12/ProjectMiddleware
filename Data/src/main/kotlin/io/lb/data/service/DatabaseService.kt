package io.lb.data.service

import io.lb.common.error.MiddlewareException
import io.lb.data.model.MappedApi
import io.lb.data.model.MappedRoute
import kotlin.jvm.Throws

/**
 * Service interface for interacting with the database.
 */
interface DatabaseService {
    /**
     * Queries all mapped APIs.
     *
     * @return A list of all mapped APIs.
     */
    suspend fun queryAllMappedRoutes(): List<MappedRoute>

    /**
     * Queries all mapped APIs.
     *
     * @return A list of all mapped APIs.
     */
    suspend fun queryMappedApi(apiUuid: String): MappedApi?

    /**
     * Queries all mapped routes for a given API.
     *
     * @param apiUuid The UUID of the API to query.
     * @return A list of all mapped routes for the given API.
     */
    @Throws(MiddlewareException::class)
    suspend fun queryMappedRoutes(apiUuid: String): List<MappedRoute>

    /**
     * Creates a mapped route.
     *
     * @param route The route to create.
     */
    @Throws(MiddlewareException::class)
    suspend fun createMappedRoute(route: MappedRoute)

    /**
     * Updates a mapped route.
     *
     * @param route The route to update.
     */
    @Throws(MiddlewareException::class)
    suspend fun updateMappedRoute(route: MappedRoute)

    /**
     * Creates a mapped API.
     *
     * @param api The API to create.
     * @return The UUID of the created API.
     */
    suspend fun createMappedApi(api: MappedApi): String

    /**
     * Updates a mapped API.
     *
     * @param api The API to update.
     */
    @Throws(MiddlewareException::class)
    suspend fun updateMappedApi(api: MappedApi)
}

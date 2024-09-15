package io.lb.common.data.service

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedRoute
import io.lb.common.shared.error.MiddlewareException
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
    suspend fun queryMappedApi(originalBaseUrl: String): MappedApi?

    /**
     * Queries all mapped routes for a given API.
     *
     * @param originalBaseUrl The base URL of the original API.
     * @return A list of all mapped routes for the given API.
     */
    @Throws(MiddlewareException::class)
    suspend fun queryMappedRoutes(originalBaseUrl: String): List<MappedRoute>

    /**
     * Creates a mapped route.
     *
     * @param route The route to create.
     */
    @Throws(MiddlewareException::class)
    suspend fun createMappedRoute(route: MappedRoute): String

    /**
     * Updates a mapped route.
     *
     * @param route The route to update.
     */
    @Throws(MiddlewareException::class)
    suspend fun updateMappedRoute(route: MappedRoute): String

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

    /**
     * Deletes a mapped API.
     */
    suspend fun close()
}

package io.lb.middleware.domain.repository

import io.lb.common.shared.flow.Resource
import kotlinx.coroutines.flow.Flow

/**
 * MiddlewareRepository is an interface that defines the methods to be implemented.
 */
interface MiddlewareRepository {
    /**
     * Starts the middleware.
     */
    fun startMiddleware(): Flow<Resource<Unit>>

    /**
     * Configures the stored mapped routes.
     */
    fun configureStoredMappedRoutes(): Flow<Resource<String>>

    /**
     * Stops the middleware.
     */
    suspend fun stopMiddleware()
}

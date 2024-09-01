package io.lb.middleware.domain.repository

import io.lb.common.shared.flow.Resource
import kotlinx.coroutines.flow.Flow

interface MiddlewareRepository {
    fun startMiddleware(): Flow<Resource<Unit>>
    fun createMappedRoutes(): Flow<Resource<String>>
    fun createGenericRoutes(): Flow<Resource<Unit>>
}

package io.lb.middleware.data.repository

import io.lb.common.shared.flow.Resource
import io.lb.middleware.data.datasource.MiddlewareDataSource
import io.lb.middleware.domain.repository.MiddlewareRepository
import kotlinx.coroutines.flow.Flow

class MiddlewareRepositoryImpl(
    private val dataSource: MiddlewareDataSource
) : MiddlewareRepository {
    override fun startMiddleware(): Flow<Resource<Unit>> {
        TODO()
    }

    override fun createMappedRoutes(): Flow<Resource<String>> {
        TODO()
    }

    override fun createGenericRoutes(): Flow<Resource<Unit>> {
        TODO()
    }
}

package io.lb.middleware.data.repository

import io.lb.common.shared.error.MiddlewareException
import io.lb.common.shared.flow.Resource
import io.lb.middleware.data.datasource.MiddlewareDataSource
import io.lb.middleware.domain.repository.MiddlewareRepository
import kotlinx.coroutines.flow.flow

/**
 * MiddlewareRepositoryImpl is the implementation of the MiddlewareRepository interface.
 * It is responsible for starting the middleware and configuring the stored mapped routes.
 *
 * @property dataSource Data source for the middleware.
 */
class MiddlewareRepositoryImpl(
    private val dataSource: MiddlewareDataSource
) : MiddlewareRepository {
    override fun startMiddleware() = flow {
        try {
            dataSource.configGenericRoutes()
            dataSource.configStoredMappedRoutes()
            emit(Resource.Success(Unit))
        } catch (e: MiddlewareException) {
            emit(Resource.Error(e.message ?: "Internal error"))
        }
    }

    override fun configureStoredMappedRoutes() = flow {
        try {
            val mappedRoutesSize = dataSource.configStoredMappedRoutes()
            emit(Resource.Success("Created $mappedRoutesSize mapped routes"))
        } catch (e: MiddlewareException) {
            emit(Resource.Error(e.message ?: "Internal error"))
        }
    }

    override fun stopMiddleware() {
        dataSource.stopMiddleware()
    }
}

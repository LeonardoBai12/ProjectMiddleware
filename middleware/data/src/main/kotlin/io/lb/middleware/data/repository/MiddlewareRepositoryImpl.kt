package io.lb.middleware.data.repository

import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.lb.common.data.model.MappedRoute
import io.lb.common.shared.error.MiddlewareException
import io.lb.common.shared.flow.Resource
import io.lb.middleware.data.datasource.MiddlewareDataSource
import io.lb.middleware.domain.repository.MiddlewareRepository
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.VisibleForTesting

/**
 * MiddlewareRepositoryImpl is the implementation of the MiddlewareRepository interface.
 * It is responsible for starting the middleware and configuring the stored mapped routes.
 *
 * @property dataSource Data source for the middleware.
 */
internal class MiddlewareRepositoryImpl(
    private val dataSource: MiddlewareDataSource
) : MiddlewareRepository {
    override fun startMiddleware() = flow {
        try {
            dataSource.configGenericRoutes {
                createMappedRoute(it)
            }
            dataSource.configStoredMappedRoutes()
            emit(Resource.Success(Unit))
        } catch (e: MiddlewareException) {
            emit(Resource.Error(e.message ?: "Internal error"))
        }
    }

    @VisibleForTesting
    private suspend fun createMappedRoute(mappedRoute: MappedRoute): MappedRoute {
        val localApi = dataSource.queryMappedApi(mappedRoute.mappedApi.originalApi.baseUrl) ?: run {
            dataSource.createMappedApi(mappedRoute.mappedApi)
            mappedRoute.mappedApi
        }

        val routes = dataSource.queryMappedRoutes(localApi.originalApi.baseUrl)
        val localRoute = routes.find { it.originalRoute.path == mappedRoute.originalRoute.path }

        localRoute?.takeIf {
            dataSource.hasSameConfigs(it, mappedRoute)
        }?.let {
            throw MiddlewareException(
                code = HttpStatusCode.Conflict.value,
                message = "Route already exists with the exact same configuration. Path: ${it.path}"
            )
        }

        dataSource.getMappedResponse(mappedRoute).takeIf {
            HttpStatusCode.fromValue(it.statusCode).isSuccess().not()
        }?.let {
            throw MiddlewareException(
                code = it.statusCode,
                message = it.body ?: "Failed to get response from original server."
            )
        }

        val uuid = dataSource.createMappedRoute(mappedRoute)
        val remoteRoute = mappedRoute.copy(
            uuid = uuid,
            path = "v1/$uuid/${mappedRoute.path}"
        )

        dataSource.configureMappedRoute(remoteRoute)
        return remoteRoute
    }

    override fun configureStoredMappedRoutes() = flow {
        try {
            val mappedRoutesSize = dataSource.configStoredMappedRoutes()
            emit(Resource.Success("Created $mappedRoutesSize mapped routes"))
        } catch (e: MiddlewareException) {
            emit(Resource.Error(e.message ?: "Internal error"))
        }
    }

    override suspend fun stopMiddleware() {
        dataSource.stopMiddleware()
    }

    override suspend fun validateUser(
        secret: String,
        audience: String,
        issuer: String,
        userId: String,
        email: String,
        expiration: Long
    ): Boolean {
        return dataSource.validateUser(
            secret = secret,
            audience = audience,
            issuer = issuer,
            userId = userId,
            email = email,
            expiration = expiration
        )
    }
}

package io.lb.middleware.data.datasource

import io.ktor.http.HttpStatusCode
import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.service.ClientService
import io.lb.common.data.service.DatabaseService
import io.lb.common.data.service.MapperService
import io.lb.common.data.service.ServerService
import io.lb.common.shared.error.MiddlewareException
import org.jetbrains.annotations.VisibleForTesting

/**
 * MiddlewareDataSource is responsible for configuring the middleware.
 *
 * @property clientService Service to make requests to the original server.
 * @property databaseService Service to interact with the database.
 * @property serverService Service to configure the server.
 * @property mapperService Service to map the responses.
 */
internal class MiddlewareDataSource(
    private val clientService: ClientService,
    private val databaseService: DatabaseService,
    private val serverService: ServerService,
    private val mapperService: MapperService
) {
    /**
     * Configures the generic routes.
     *
     * @throws MiddlewareException If an error occurs while configuring the routes.
     */
    @Throws(MiddlewareException::class)
    fun configGenericRoutes() {
        serverService.startQueryAllRoutesRoute {
            kotlin.runCatching {
                val routes = databaseService.queryAllMappedRoutes()
                routes.map { route -> route.copy(rulesAsString = null) }
            }.getOrElse {
                emptyList()
            }
        }
        serverService.startGenericMappingRoute { mappedRoute ->
            kotlin.runCatching {
                val route = if (mapperService.validateMappingRules(mappedRoute.rulesAsString.orEmpty())) {
                    createMappedRoute(mappedRoute)
                } else {
                    throw MiddlewareException(
                        code = HttpStatusCode.BadRequest.value,
                        message = "Invalid mapping rules."
                    )
                }
                route.path
            }.getOrElse {
                it.message.toString()
            }
        }
        serverService.startPreviewRoute { originalResponse, mappingRules ->
            mapperService.responseJsonPreview(
                mappingRules,
                originalResponse
            )
        }
    }

    /**
     * Configures the stored mapped routes.
     *
     * @return The number of mapped routes created.
     * @throws MiddlewareException If an error occurs while configuring the routes.
     */
    @Throws(MiddlewareException::class)
    suspend fun configStoredMappedRoutes(): Int {
        val localRoutes = databaseService.queryAllMappedRoutes()
        serverService.createMappedRoutes(localRoutes) { mappedRoute ->
            kotlin.runCatching {
                getMappedResponse(mappedRoute)
            }.getOrElse {
                val e = it as MiddlewareException
                MappedResponse(
                    statusCode = e.code,
                    body = e.message
                )
            }
        }
        return localRoutes.size
    }

    @VisibleForTesting
    @Throws(MiddlewareException::class)
    suspend fun createMappedRoute(mappedRoute: MappedRoute): MappedRoute {
        val localApi = databaseService.queryMappedApi(mappedRoute.mappedApi.originalApi.baseUrl) ?: run {
            databaseService.createMappedApi(mappedRoute.mappedApi)
            mappedRoute.mappedApi
        }

        val routes = databaseService.queryMappedRoutes(localApi.originalApi.baseUrl)
        val localRoute = routes.find { it.originalRoute.path == mappedRoute.originalRoute.path }

        localRoute?.takeIf {
            hasSameConfigs(it, mappedRoute)
        }?.let {
            throw MiddlewareException(
                code = HttpStatusCode.Conflict.value,
                message = "Route already exists with the exact same configuration. Path: ${it.path}"
            )
        }

        getMappedResponse(mappedRoute).takeIf {
            it.statusCode != HttpStatusCode.OK.value
        }?.let {
            throw MiddlewareException(
                code = it.statusCode,
                message = it.body ?: "Failed to get response from original server."
            )
        }

        val uuid = databaseService.createMappedRoute(mappedRoute)
        val remoteRoute = mappedRoute.copy(
            uuid = uuid,
            path = "v1/$uuid/${mappedRoute.path}"
        )

        configureMappedRoute(remoteRoute)
        return remoteRoute
    }

    private fun hasSameConfigs(
        localRoute: MappedRoute,
        mappedRoute: MappedRoute,
    ) = localRoute.originalRoute.method == mappedRoute.originalRoute.method &&
        localRoute.preConfiguredQueries == mappedRoute.preConfiguredQueries &&
        localRoute.preConfiguredHeaders == mappedRoute.preConfiguredHeaders &&
        localRoute.preConfiguredBody == mappedRoute.preConfiguredBody &&
        localRoute.rulesAsString == mappedRoute.rulesAsString

    @Throws(MiddlewareException::class)
    private suspend fun configureMappedRoute(mappedRoute: MappedRoute) {
        serverService.createMappedRoute(mappedRoute) {
            getMappedResponse(mappedRoute)
        }
    }

    @VisibleForTesting
    @Throws(MiddlewareException::class)
    suspend fun getMappedResponse(mappedRoute: MappedRoute): MappedResponse {
        val originalResponse = clientService.request(
            route = mappedRoute.originalRoute,
            preConfiguredQueries = mappedRoute.preConfiguredQueries,
            preConfiguredHeaders = mappedRoute.preConfiguredHeaders,
            preConfiguredBody = mappedRoute.preConfiguredBody
        )
        if (originalResponse.statusCode != HttpStatusCode.OK.value) {
            throw MiddlewareException(
                code = originalResponse.statusCode,
                message = originalResponse.body ?: "Failed to get response from original server."
            )
        }
        val mappedResponse = mapperService.mapResponse(
            originalResponse = originalResponse,
            mappingRules = mappedRoute.rulesAsString.orEmpty()
        )
        return mappedResponse
    }

    /**
     * Stops the middleware.
     */
    suspend fun stopMiddleware() {
        serverService.stopServer()
        databaseService.close()
    }
}

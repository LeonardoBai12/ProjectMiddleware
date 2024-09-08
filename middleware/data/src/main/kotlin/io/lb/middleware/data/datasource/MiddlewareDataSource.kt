package io.lb.middleware.data.datasource

import io.ktor.http.HttpStatusCode
import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.service.ClientService
import io.lb.common.data.service.DatabaseService
import io.lb.common.data.service.MapperService
import io.lb.common.data.service.ServerService
import io.lb.common.shared.error.MiddlewareException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    private val json = Json { this.prettyPrint = true }

    /**
     * Configures the generic routes.
     *
     * @throws MiddlewareException If an error occurs while configuring the routes.
     */
    @Throws(MiddlewareException::class)
    fun configGenericRoutes() {
        serverService.startQueryAllRoutesRoute {
            try {
                val routes = databaseService.queryAllMappedRoutes()
                json.encodeToString(routes.map { it.copy(rulesAsString = null) })
            } catch (e: MiddlewareException) {
                e.message.toString()
            }
        }
        serverService.startGenericMappingRoute {
            try {
                val route = createMappedRoute(it)
                route.path
            } catch (e: MiddlewareException) {
                e.message.toString()
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
            try {
                getMappedResponse(mappedRoute)
            } catch (e: MiddlewareException) {
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
        var localApi = databaseService.queryMappedApi(mappedRoute.mappedApi.originalApi.baseUrl)
        if (localApi == null) {
            val uuid = databaseService.createMappedApi(mappedRoute.mappedApi)
            localApi = MappedApi(
                uuid = uuid,
                originalApi = mappedRoute.mappedApi.originalApi
            )
        }

        val routes = databaseService.queryMappedRoutes(localApi.originalApi.baseUrl)
        val localRoute = routes.find { it.originalRoute.path == mappedRoute.originalRoute.path }

        val uuid = if (localRoute != null) {
            databaseService.updateMappedRoute(mappedRoute.copy(uuid = localRoute.uuid))
        } else {
            databaseService.createMappedRoute(mappedRoute)
        }

        val remoteRoute = mappedRoute.copy(
            uuid = uuid,
            path = "v1/$uuid/${mappedRoute.path}"
        )

        configureMappedRoute(localRoute ?: remoteRoute)
        return localRoute ?: remoteRoute
    }

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
    fun stopMiddleware() {
        serverService.stopServer()
    }
}

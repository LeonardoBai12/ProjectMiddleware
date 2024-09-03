package io.lb.middleware.data.datasource

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.service.ClientService
import io.lb.common.data.service.DatabaseService
import io.lb.common.data.service.MapperService
import io.lb.common.data.service.ServerService
import io.lb.common.shared.error.MiddlewareException
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
class MiddlewareDataSource(
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
        serverService.startGenericMappingRoute {
            createMappedRoute(it)
            "/v1/${it.uuid}/${it.path}"
        }
        serverService.startPreviewRoute { originalResponse, mappingRules ->
            mapperService.responseJsonPreview(
                mappingRules,
                Json.decodeFromString(originalResponse),
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
            getMappedResponse(mappedRoute)
        }
        return localRoutes.size
    }

    @Throws(MiddlewareException::class)
    @VisibleForTesting
    suspend fun createMappedRoute(mappedRoute: MappedRoute) {
        databaseService.createMappedRoute(mappedRoute)
        configureMappedRoute(mappedRoute)
    }

    @Throws(MiddlewareException::class)
    private suspend fun configureMappedRoute(mappedRoute: MappedRoute) {
        serverService.createMappedRoute(mappedRoute) {
            getMappedResponse(mappedRoute)
        }
    }

    @VisibleForTesting
    suspend fun getMappedResponse(mappedRoute: MappedRoute): MappedResponse {
        val originalResponse = clientService.request(
            route = mappedRoute.originalRoute,
            preConfiguredQueries = mappedRoute.preConfiguredQueries,
            preConfiguredHeaders = mappedRoute.preConfiguredHeaders,
            preConfiguredBody = mappedRoute.preConfiguredBody
        )
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

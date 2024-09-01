package io.lb.middleware.data.datasource

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.service.ClientService
import io.lb.common.data.service.DatabaseService
import io.lb.common.data.service.MapperService
import io.lb.common.data.service.ServerService
import kotlinx.serialization.json.Json

class MiddlewareDataSource(
    private val clientService: ClientService,
    private val databaseService: DatabaseService,
    private val serverService: ServerService,
    private val mapperService: MapperService
) {
    fun configGenericRoutes() {
        serverService.startGenericMappingRoute {
            databaseService.createMappedRoute(it)
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

    suspend fun configStoredMappedRoutes() {
        val localRoutes = databaseService.queryAllMappedRoutes()
        serverService.createMappedRoutes(localRoutes) { mappedRoute ->
            getMappedResponse(mappedRoute)
        }
    }

    private suspend fun createMappedRoute(mappedRoute: MappedRoute) {
        serverService.createMappedRoute(mappedRoute) {
            getMappedResponse(mappedRoute)
        }
    }

    private suspend fun getMappedResponse(mappedRoute: MappedRoute): MappedResponse {
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
}

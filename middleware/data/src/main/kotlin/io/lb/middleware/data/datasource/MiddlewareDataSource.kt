package io.lb.middleware.data.datasource

import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.lb.common.data.model.MappedApi
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
    fun configGenericRoutes(onCreateMappedRoute: suspend (MappedRoute) -> MappedRoute) {
        serverService.startQueryAllRoutesRoute {
            kotlin.runCatching {
                val routes = databaseService.queryAllMappedRoutes()
                routes.map { route -> route.copy(rulesAsString = null) }
            }.getOrElse {
                emptyList()
            }
        }
        serverService.startGenericMappingRoute { mappedRoute ->
            val route = if (mapperService.validateMappingRules(mappedRoute.rulesAsString.orEmpty())) {
                onCreateMappedRoute(mappedRoute)
            } else {
                throw MiddlewareException(
                    code = HttpStatusCode.BadRequest.value,
                    message = "Invalid mapping rules."
                )
            }
            route.path
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

    fun hasSameConfigs(
        localRoute: MappedRoute,
        mappedRoute: MappedRoute,
    ) = localRoute.originalRoute.method == mappedRoute.originalRoute.method &&
        localRoute.preConfiguredQueries == mappedRoute.preConfiguredQueries &&
        localRoute.preConfiguredHeaders == mappedRoute.preConfiguredHeaders &&
        localRoute.preConfiguredBody == mappedRoute.preConfiguredBody &&
        localRoute.rulesAsString == mappedRoute.rulesAsString

    @Throws(MiddlewareException::class)
    suspend fun configureMappedRoute(mappedRoute: MappedRoute) {
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
        if (HttpStatusCode.fromValue(originalResponse.statusCode).isSuccess().not()) {
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

    /**
     * Queries the mapped API.
     *
     * @param baseUrl The base URL of the API.
     * @return The mapped API.
     */
    suspend fun queryMappedApi(baseUrl: String): MappedApi? {
        return databaseService.queryMappedApi(baseUrl)
    }

    /**
     * Creates the mapped API.
     *
     * @param mappedApi The mapped API.
     */
    suspend fun createMappedApi(mappedApi: MappedApi) {
        databaseService.createMappedApi(mappedApi)
    }

    /**
     * Queries the mapped routes.
     *
     * @param baseUrl The base URL of the API.
     * @return The mapped routes.
     */
    suspend fun queryMappedRoutes(baseUrl: String): List<MappedRoute> {
        return databaseService.queryMappedRoutes(baseUrl)
    }

    /**
     * Creates the mapped route.
     *
     * @param mappedRoute The mapped route.
     * @return The UUID of the mapped route.
     */
    suspend fun createMappedRoute(mappedRoute: MappedRoute): String {
        return databaseService.createMappedRoute(mappedRoute)
    }

    suspend fun validateUser(
        secret: String,
        audience: String,
        issuer: String,
        userId: String,
        email: String,
        expiration: Long,
    ): Boolean {
        return clientService.validateUser(
            secret = secret,
            audience = audience,
            issuer = issuer,
            userId = userId,
            email = email,
            expiration = expiration
        )
    }
}

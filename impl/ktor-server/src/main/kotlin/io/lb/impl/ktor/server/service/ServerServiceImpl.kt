package io.lb.impl.ktor.server.service

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.method
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.toMap
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.service.ServerService

/**
 * Implementation of the server service.
 *
 * @property application The application on which the server is running.
 */
class ServerServiceImpl(
    private val application: Application,
) : ServerService {
    override fun createMappedRoute(
        mappedRoute: MappedRoute,
        onRequest: (OriginalRoute, Map<String, String>) -> OriginalResponse
    ) {
        application.routing {
            method(HttpMethod.parse(mappedRoute.method.name)) {
                route("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                    handle {
                        val queries = call.request.queryParameters.toMap().mapValues { it.value.first() }
                        val originalRoute = mappedRoute.originalRoute.copy()
                        val response = onRequest(originalRoute, queries)

                        call.respond(
                            HttpStatusCode.fromValue(response.statusCode),
                            response.body ?: "Internal server error"
                        )
                    }
                }
            }
        }
    }

    override fun createMappedRoutes(
        mappedRoutes: List<MappedRoute>,
        onRequest: (OriginalRoute, Map<String, String>) -> OriginalResponse
    ) {
        mappedRoutes.forEach {
            createMappedRoute(it, onRequest)
        }
    }
}

package io.lb.server.service

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.method
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.lb.data.model.MappedRoute
import io.lb.data.model.OriginalResponse
import io.lb.data.model.OriginalRoute
import io.lb.data.service.ServerService

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
        onRequest: (OriginalRoute) -> OriginalResponse
    ) {
        application.routing {
            method(HttpMethod.parse(mappedRoute.method.name)) {
                route(mappedRoute.path) {
                    handle {
                        val originalRoute = mappedRoute.originalRoute
                        val response = onRequest(originalRoute)

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
        onCompletion: (OriginalRoute) -> OriginalResponse
    ) {
        mappedRoutes.forEach {
            createMappedRoute(it, onCompletion)
        }
    }
}

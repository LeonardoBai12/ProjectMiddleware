package io.lb.impl.ktor.server.service

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.method
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareHttpMethods
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
            when (mappedRoute.originalRoute.method) {
                MiddlewareHttpMethods.Get -> {
                    get("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Post -> {
                    post("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Put -> {
                    put("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Delete -> {
                    delete("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Patch -> {
                    patch("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Head -> {
                    head("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onRequest(mappedRoute, onRequest)
                    }
                }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onRequest(
        mappedRoute: MappedRoute,
        onRequest: (OriginalRoute, Map<String, String>) -> OriginalResponse,
    ) {
        val queries = call.request.queryParameters.toMap().mapValues { query ->
            query.value.first()
        }
        val originalRoute = mappedRoute.originalRoute.copy()
        val response = onRequest(originalRoute, queries)

        call.respond(
            HttpStatusCode.fromValue(response.statusCode),
            response.body ?: "Internal server error"
        )
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

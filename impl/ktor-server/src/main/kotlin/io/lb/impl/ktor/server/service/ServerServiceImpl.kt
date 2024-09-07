package io.lb.impl.ktor.server.service

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.request.receiveNullable
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.common.data.service.ServerService
import io.lb.impl.ktor.server.model.MappedRouteParameter
import io.lb.impl.ktor.server.model.PreviewRequestBody

/**
 * Implementation of the server service.
 *
 * @property engine The engine of the application.
 */
internal class ServerServiceImpl(
    private val engine: NettyApplicationEngine
) : ServerService {
    override fun startGenericMappingRoute(onReceive: suspend (MappedRoute) -> String) {
        engine.application.routing {
            post("v1/mapping") {
                val parameter = call.receiveNullable<MappedRouteParameter>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val mappedRouteUrl = onReceive(parameter.toMappedRoute())
                call.respond(HttpStatusCode.Created, mappedRouteUrl)
            }
        }
    }

    override fun startPreviewRoute(onReceive: (String, String) -> String) {
        engine.application.routing {
            get("v1/preview") {
                val parameter = call.receiveNullable<PreviewRequestBody>()
                val originalResponse = parameter?.originalResponse ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val mappingRules = parameter.mappingRules.toString()
                val mappedResponse = onReceive(originalResponse.toString(), mappingRules)
                call.respond(HttpStatusCode.OK, mappedResponse)
            }
        }
    }

    override fun createMappedRoute(
        mappedRoute: MappedRoute,
        onRequest: suspend (MappedRoute) -> MappedResponse
    ) {
        engine.application.routing {
            when (mappedRoute.originalRoute.method) {
                MiddlewareHttpMethods.Get -> {
                    get("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onServerRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Post -> {
                    post("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onServerRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Put -> {
                    put("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onServerRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Delete -> {
                    delete("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onServerRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Patch -> {
                    patch("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onServerRequest(mappedRoute, onRequest)
                    }
                }
                MiddlewareHttpMethods.Head -> {
                    head("v1/${mappedRoute.uuid}/${mappedRoute.path}") {
                        onServerRequest(mappedRoute, onRequest)
                    }
                }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onServerRequest(
        mappedRoute: MappedRoute,
        onRequest: suspend (MappedRoute) -> MappedResponse,
    ) {
        val queries = call.request.queryParameters.toMap().mapValues { query ->
            query.value.first()
        }
        val headers = call.request.headers.toMap().mapValues { header ->
            header.value.first()
        }
        val body = call.receiveText()
        mappedRoute.originalRoute = mappedRoute.originalRoute.copy(
            queries = queries,
            headers = headers,
            body = body
        )
        val response = onRequest(mappedRoute)

        call.respond(
            HttpStatusCode.fromValue(response.statusCode),
            response.body ?: "Internal server error"
        )
    }

    override fun createMappedRoutes(
        mappedRoutes: List<MappedRoute>,
        onEachRequest: suspend (MappedRoute) -> MappedResponse
    ) {
        mappedRoutes.forEach {
            createMappedRoute(it, onEachRequest)
        }
    }

    override fun stopServer() {
        engine.stop(gracePeriodMillis = 3000, timeoutMillis = 5000)
    }
}

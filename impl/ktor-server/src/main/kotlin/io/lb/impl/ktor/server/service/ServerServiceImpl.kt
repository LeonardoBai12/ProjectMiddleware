package io.lb.impl.ktor.server.service

import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.defaultTextContentType
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
import io.lb.common.shared.error.MiddlewareException
import io.lb.impl.ktor.server.model.MappedRouteParameter
import io.lb.impl.ktor.server.model.PreviewRequestBody
import kotlinx.serialization.json.JsonObject

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
            authenticate {
                post("v1/mapping") {
                    val parameter = call.receiveNullable<MappedRouteParameter>() ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                    val mappedRouteUrl = runCatching {
                        onReceive(parameter.toMappedRoute())
                    }.getOrElse { error ->
                        when (error) {
                            is IllegalArgumentException -> call.respond(
                                HttpStatusCode.BadRequest,
                                error.localizedMessage
                            )

                            is MiddlewareException -> call.respond(
                                HttpStatusCode.fromValue(error.code),
                                error.localizedMessage
                            )

                            else -> call.respond(HttpStatusCode.BadRequest, error.localizedMessage)
                        }
                        return@post
                    }
                    call.respond(HttpStatusCode.Created, mappedRouteUrl)
                }
            }
        }
    }

    override fun startQueryAllRoutesRoute(onReceive: suspend (String) -> List<MappedRoute>) {
        engine.application.routing {
            authenticate {
                get("v1/routes") {
                    val routes = kotlin.runCatching {
                        onReceive("v1/routes")
                    }.getOrElse { error ->
                        when (error) {
                            is MiddlewareException -> call.respond(
                                HttpStatusCode.fromValue(error.code),
                                error.localizedMessage
                            )

                            else -> call.respond(HttpStatusCode.BadRequest, error.localizedMessage)
                        }
                        return@get
                    }
                    call.respond(HttpStatusCode.OK, routes)
                }
            }
        }
    }

    override fun startPreviewRoute(onReceive: (String, String) -> String) {
        engine.application.routing {
            authenticate {
                post("v1/preview") {
                    val parameter = call.receiveNullable<PreviewRequestBody>()
                    val originalResponse = parameter?.originalResponse ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }
                    val mappingRules = kotlin.runCatching {
                        parameter.mappingRules.toString()
                    }.getOrElse { error ->
                        when (error) {
                            is MiddlewareException -> call.respond(
                                HttpStatusCode.fromValue(error.code),
                                error.localizedMessage
                            )

                            else -> call.respond(HttpStatusCode.BadRequest, error.localizedMessage)
                        }
                        return@post
                    }
                    val mappedResponse = kotlin.runCatching {
                        onReceive(originalResponse.toString(), mappingRules)
                    }.getOrElse { error ->
                        when (error) {
                            is MiddlewareException -> call.respond(
                                HttpStatusCode.fromValue(error.code),
                                error.localizedMessage
                            )

                            else -> call.respond(HttpStatusCode.BadRequest, error.localizedMessage)
                        }
                        return@post
                    }
                    call.respond(HttpStatusCode.OK, mappedResponse)
                }
            }
        }
    }

    override fun createMappedRoute(
        mappedRoute: MappedRoute,
        onRequest: suspend (MappedRoute) -> MappedResponse
    ) {
        engine.application.routing {
            authenticate {
                when (mappedRoute.method) {
                    MiddlewareHttpMethods.Get -> {
                        get(mappedRoute.path) {
                            onServerRequest(mappedRoute, onRequest)
                        }
                    }

                    MiddlewareHttpMethods.Post -> {
                        post(mappedRoute.path) {
                            onServerRequest(mappedRoute, onRequest)
                        }
                    }

                    MiddlewareHttpMethods.Put -> {
                        put(mappedRoute.path) {
                            onServerRequest(mappedRoute, onRequest)
                        }
                    }

                    MiddlewareHttpMethods.Delete -> {
                        delete(mappedRoute.path) {
                            onServerRequest(mappedRoute, onRequest)
                        }
                    }

                    MiddlewareHttpMethods.Patch -> {
                        patch(mappedRoute.path) {
                            onServerRequest(mappedRoute, onRequest)
                        }
                    }

                    MiddlewareHttpMethods.Head -> {
                        head(mappedRoute.path) {
                            onServerRequest(mappedRoute, onRequest)
                        }
                    }
                }
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.onServerRequest(
        mappedRoute: MappedRoute,
        onRequest: suspend (MappedRoute) -> MappedResponse,
    ) {
        call.defaultTextContentType(ContentType.Application.Json)
        val queries = call.request.queryParameters.toMap().mapValues { query ->
            query.value.first()
        }
        val headers = call.request.headers.toMap().mapValues { header ->
            header.value.first()
        }
        val body = try {
            call.receiveNullable<JsonObject>()
        } catch (e: Exception) {
            null
        }
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

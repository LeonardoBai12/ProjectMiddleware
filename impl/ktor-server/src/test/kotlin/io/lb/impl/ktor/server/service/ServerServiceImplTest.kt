package io.lb.impl.ktor.server.service

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareAuthHeader
import io.lb.common.data.request.MiddlewareAuthHeaderType
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.impl.ktor.server.model.MappedApiParameter
import io.lb.impl.ktor.server.model.MappedRouteParameter
import io.lb.impl.ktor.server.model.OriginalApiParameter
import io.lb.impl.ktor.server.model.OriginalRouteParameter
import io.lb.impl.ktor.server.model.PreviewRequestBody
import io.lb.impl.ktor.server.util.setupApplication
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ServerServiceImplTest {
    private lateinit var serverService: ServerServiceImpl

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `When uses Post method on mapping route, expect unsupported media type`() =
        serverServiceTestApplication(MiddlewareHttpMethods.Post) {
            val response = client.post("v1/mapping") {
                contentType(ContentType.Application.Json)
            }
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }

    @Test
    fun `When uses Post method on mapping route, expect bad request`() =
        serverServiceTestApplication(MiddlewareHttpMethods.Post) {
            val response = client.post("v1/mapping") {
                contentType(ContentType.Application.Json)
                setBody("{}")
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun `When uses Post method on mapping route, expect created`() =
        serverServiceTestApplication(MiddlewareHttpMethods.Post) {
            val mappedRouteParameter = createMappedRoute()
            val response = client.post("v1/mapping") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(mappedRouteParameter))
            }
            assertEquals(HttpStatusCode.Created, response.status)
        }

    @Test
    fun `When uses Get method on preview route, expect OK`() =
        serverServiceTestApplication(MiddlewareHttpMethods.Get) {
            val response = client.get("v1/preview") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(createPreviewRequest()))
            }
            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `When uses Get method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Get) {
        val response = client.get("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            contentType(ContentType.Application.Json)
            header("header-lb", "example")
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
            setBody("""{"key":"request"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `When uses Delete method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Delete) {
        val response = client.delete("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            contentType(ContentType.Application.Json)
            header("header-lb", "example")
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
            setBody("""{"key":"request"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    private fun serverServiceTestApplication(
        method: MiddlewareHttpMethods,
        block: suspend ApplicationTestBuilder.() -> Unit
    ) {
        testApplication {
            setupApplication()
            application {
                val (
                    testMappedRoute,
                    onRequestMock: (
                        MappedRoute
                    ) -> MappedResponse
                ) = setupService(method)
                serverService.createMappedRoute(testMappedRoute, onRequestMock)
                serverService.startGenericMappingRoute { "Received" }
                serverService.startPreviewRoute { _, _ -> "Received" }
            }
            block()
        }
    }

    @Test
    fun `When uses Post method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Post) {
        val response = client.post("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            contentType(ContentType.Application.Json)
            header("header-lb", "example")
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
            setBody("""{"key":"request"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `When uses Put method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Put) {
        val response = client.put("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            contentType(ContentType.Application.Json)
            header("header-lb", "example")
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
            setBody("""{"key":"request"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `When uses Head method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Head) {
        val response = client.head("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            contentType(ContentType.Application.Json)
            header("header-lb", "example")
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
            setBody("""{"key":"request"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `When uses Patch method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Patch) {
        val response = client.patch("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            contentType(ContentType.Application.Json)
            header("header-lb", "example")
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
            setBody("""{"key":"request"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    private fun Application.setupService(
        method: MiddlewareHttpMethods
    ): Pair<MappedRoute, (MappedRoute) -> MappedResponse> {
        val route = OriginalRoute(
            path = "test-route",
            method = method,
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.None, "Authenticated"),
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"key":"request"}"""
        )
        val testMappedRoute = MappedRoute(
            uuid = "b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b",
            method = method,
            path = "test-path",
            rulesAsString = "test-rules",
            originalRoute = route,
            mappedApi = MappedApi(
                "a1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b",
                originalApi = OriginalApi(baseUrl = "https://10.0.2.2:8885/")
            ),
        )

        val testResponse = MappedResponse(
            statusCode = 200,
            body = "Test Body"
        )

        val onRequestMock: (MappedRoute) -> MappedResponse =
            {
                it.originalRoute.queries.apply {
                    assertEquals("timestamp", get("sortBy"))
                    assertEquals("asc", get("order"))
                }
                it.originalRoute.headers.apply {
                    assertEquals("application/json", get("Content-Type"))
                    assertEquals("example", get("header-lb"))
                }
                assertEquals("""{"key":"request"}""", it.originalRoute.body)
                testResponse
            }

        val engine: NettyApplicationEngine = mockk(relaxed = true)
        every { engine.application } returns this

        serverService = ServerServiceImpl(engine)
        return Pair(testMappedRoute, onRequestMock)
    }

    private fun createPreviewRequest(): PreviewRequestBody {
        return PreviewRequestBody(
            originalResponse = Json.parseToJsonElement("{}").jsonObject,
            mappingRules = Json.parseToJsonElement("{}").jsonObject
        )
    }

    private fun createMappedRoute(): MappedRouteParameter {
        return MappedRouteParameter(
            path = "getTeryiaki",
            method = MiddlewareHttpMethods.Get,
            originalRoute = OriginalRouteParameter(
                path = "json/v1/1/lookup.php?i=52772",
                method = MiddlewareHttpMethods.Get,
                originalApi = OriginalApiParameter("https://www.themealdb.com/api/"),
                body = null
            ),
            rulesAsString = "RulesAsString"
        )
    }
}

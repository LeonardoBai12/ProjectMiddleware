package io.lb.impl.ktor.server.service

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareAuthHeader
import io.lb.common.data.request.MiddlewareAuthHeaderType
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.impl.ktor.server.util.setupApplication
import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class ServerServiceImplTest {
    private lateinit var serverService: ServerServiceImpl

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `When uses Get method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Get) {
        val response = client.get("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `When uses Delete method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Delete) {
        val response = client.delete("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
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
                        OriginalRoute,
                        Map<String, String>
                    ) -> OriginalResponse
                ) = setupService(method)
                serverService.createMappedRoute(testMappedRoute, onRequestMock)
            }
            block()
        }
    }

    @Test
    fun `When uses Post method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Post) {
        val response = client.post("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `When uses Put method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Put) {
        val response = client.put("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `When uses Head method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Head) {
        val response = client.head("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `When uses Patch method, expect success`() = serverServiceTestApplication(MiddlewareHttpMethods.Patch) {
        val response = client.patch("v1/b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b/test-path") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    private fun Application.setupService(
        method: MiddlewareHttpMethods
    ): Pair<MappedRoute, (OriginalRoute, Map<String, String>) -> OriginalResponse> {
        val route = OriginalRoute(
            path = "test-route",
            method = method,
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.None, "Authenticated"),
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"key":"request"}"""
        )
        val testMappedRoute = MappedRoute(
            uuid = UUID.fromString("b1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b"),
            method = method,
            path = "test-path",
            rulesAsString = "test-rules",
            originalRoute = route,
            mappedApi = MappedApi(
                UUID.fromString("a1b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b"),
                name = "here",
                originalApi = OriginalApi(baseUrl = "https://10.0.2.2:8885/")
            ),
        )

        val testResponse = OriginalResponse(
            statusCode = 200,
            body = "Test Body"
        )

        val onRequestMock: (OriginalRoute, Map<String, String>) -> OriginalResponse = { _, parameters ->
            parameters.apply {
                assertEquals("timestamp", get("sortBy"))
                assertEquals("asc", get("order"))
            }
            testResponse
        }

        serverService = ServerServiceImpl(this)
        return Pair(testMappedRoute, onRequestMock)
    }
}

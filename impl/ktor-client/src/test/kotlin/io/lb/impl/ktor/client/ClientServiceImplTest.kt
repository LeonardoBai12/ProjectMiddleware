package io.lb.impl.ktor.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.toMap
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareAuthHeader
import io.lb.common.data.request.MiddlewareAuthHeaderType
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.impl.ktor.client.service.ClientServiceImpl
import io.mockk.unmockkAll
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ClientServiceImplTest {

    private lateinit var httpClient: HttpClient
    private lateinit var service: ClientServiceImpl
    private lateinit var mockEngine: MockEngine

    @BeforeEach
    fun setUp() {
        mockEngine = MockEngine { request ->
            if (request.method == HttpMethod.Head && request.url.host == "10.0.2.2") {
                return@MockEngine respond(
                    content = "",
                    status = HttpStatusCode.OK
                )
            }

            when (request.url.encodedPath) {
                "/test-route" -> respond(
                    content = """{"key":"response"}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf("Content-Type" to listOf("application/json"))
                )
                else -> respond(
                    content = "Not Found",
                    status = HttpStatusCode.NotFound
                )
            }
        }

        httpClient = HttpClient(mockEngine)
        service = ClientServiceImpl(httpClient)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When calls existent route, expect success`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.None, "Authenticated"),
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"key":"request"}"""
        )

        val response = service.request(route, emptyMap(), emptyMap(), null)

        assertEquals(200, response.statusCode)
        assertEquals("""{"key":"response"}""", response.body)
    }

    @Test
    fun `When calls not existent route, expect to not find it`() = runTest {
        val route = OriginalRoute(
            path = "test-route-unknown",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.None, "Authenticated"),
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"key":"request"}"""
        )

        val response = service.request(route, emptyMap(), emptyMap(), null)

        assertEquals(404, response.statusCode)
        assertEquals("Not Found", response.body)
    }

    @Test
    fun `When validates not existent api, expect to not find it`() = runTest {
        val response = service.validateApi(OriginalApi("https://10.0.1.1:2185/"))
        assertEquals(404, response.statusCode)
    }

    @Test
    fun `When validates existent route, expect to not find it`() = runTest {
        val response = service.validateApi(OriginalApi("https://10.0.2.2:8885/"))
        assertEquals(200, response.statusCode)
    }

    @Test
    fun `When URL without HTTPS protocol, expect throws exception`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("http://10.0.2.2:8282/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Basic, "Authenticated"),
            headers = mapOf("Content-Type" to "application/json"),
            body = """{"key":"request"}"""
        )

        assertThrows<IllegalArgumentException> {
            service.request(route, emptyMap(), emptyMap(), null)
        }
    }

    @Test
    fun `When URL with parameters, body and headers, expect them to be added`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8282/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Basic, "Authenticated"),
            headers = mapOf("OtherRandom" to "OtherHeader"),
            body = """{"key":"request"}"""
        )

        val response = service.request(
            route = route,
            preConfiguredQueries = mapOf("key" to "value"),
            preConfiguredHeaders = mapOf("Random" to "Header"),
            preConfiguredBody = """{"key":"value"}"""
        )
        advanceUntilIdle()
        val request = mockEngine.requestHistory.first()

        assertEquals(mapOf("key" to listOf("value")), request.url.parameters.toMap())
        assertEquals("Header", request.headers["Random"])
        assertEquals("""{"key":"value"}""", request.body.toByteArray().decodeToString())
        assertEquals(200, response.statusCode)
    }
}

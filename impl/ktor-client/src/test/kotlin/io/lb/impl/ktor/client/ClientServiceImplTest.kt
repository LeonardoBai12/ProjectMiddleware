package io.lb.impl.ktor.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
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
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ClientServiceImplTest {
    private val json = Json { ignoreUnknownKeys = true }
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

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(
                    json
                )
            }
        }
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
            body = json.decodeFromString("""{"key":"request"}""")
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
            body = json.decodeFromString("""{"key":"request"}""")
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
            body = json.decodeFromString("""{"key":"request"}""")
        )

        assertThrows<IllegalArgumentException> {
            service.request(route, emptyMap(), emptyMap(), null)
        }
    }

    @Test
    fun `When calls route with Bearer auth, expect Authorization header sent`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Bearer, "my-token"),
        )
        service.request(route, emptyMap(), emptyMap(), null)
        advanceUntilIdle()
        assertEquals("Bearer my-token", mockEngine.requestHistory.last().headers[HttpHeaders.Authorization])
    }

    @Test
    fun `When calls route with Basic auth, expect Authorization header sent`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Basic, "base64creds"),
        )
        service.request(route, emptyMap(), emptyMap(), null)
        advanceUntilIdle()
        assertEquals("Basic base64creds", mockEngine.requestHistory.last().headers[HttpHeaders.Authorization])
    }

    @Test
    fun `When calls route with no auth header, expect no Authorization sent`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = null,
        )
        service.request(route, emptyMap(), emptyMap(), null)
        advanceUntilIdle()
        assertNull(mockEngine.requestHistory.last().headers[HttpHeaders.Authorization])
    }

    @Test
    fun `When uses Head method, expect Head HTTP method sent to server`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Head,
            authHeader = null,
        )
        service.request(route, emptyMap(), emptyMap(), null)
        advanceUntilIdle()
        assertEquals(HttpMethod.Head, mockEngine.requestHistory.last().method)
    }

    @Test
    fun `When preConfiguredHeaders is empty, expect originalRoute headers used`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = null,
            headers = mapOf("X-Custom" to "custom-value", "Content-Type" to "application/json"),
        )
        service.request(route, emptyMap(), emptyMap(), null)
        advanceUntilIdle()
        assertEquals("custom-value", mockEngine.requestHistory.last().headers["X-Custom"])
    }

    @Test
    fun `When preConfiguredQueries is empty, expect originalRoute queries used`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = null,
            queries = mapOf("key" to "value"),
        )
        service.request(route, emptyMap(), emptyMap(), null)
        advanceUntilIdle()
        val params = mockEngine.requestHistory.last().url.parameters.toMap()
        assertEquals(mapOf("key" to listOf("value")), params)
    }

    @Test
    fun `When preConfiguredBody is null, expect originalRoute body used`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = null,
            headers = mapOf("Content-Type" to "application/json"),
            body = json.decodeFromString("""{"key":"from-route"}"""),
        )
        service.request(route, emptyMap(), emptyMap(), null)
        advanceUntilIdle()
        val body = mockEngine.requestHistory.last().body.toByteArray().decodeToString()
        assertEquals("""{"key":"from-route"}""", body)
    }

    @Test
    fun `When preConfiguredHeaders contains Authorization, expect authHeader takes priority`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8885/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Bearer, "correct-token"),
            headers = mapOf("Authorization" to "Bearer wrong-token"),
        )
        service.request(route, emptyMap(), emptyMap(), null)
        advanceUntilIdle()
        val auth = mockEngine.requestHistory.last().headers[HttpHeaders.Authorization]
        assertEquals("Bearer correct-token", auth)
    }

    @Test
    fun `When URL with parameters, body and headers, expect them to be added`() = runTest {
        val route = OriginalRoute(
            path = "test-route",
            originalApi = OriginalApi("https://10.0.2.2:8282/"),
            method = MiddlewareHttpMethods.Get,
            authHeader = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Basic, "Authenticated"),
            headers = mapOf("OtherRandom" to "OtherHeader"),
            body = json.decodeFromString("""{"key":"request"}""")
        )

        val response = service.request(
            route = route,
            preConfiguredQueries = mapOf("key" to "value"),
            preConfiguredHeaders = mapOf(
                "Random" to "Header",
                "Content-Type" to "application/json"
            ),
            preConfiguredBody = json.decodeFromString("""{"key":"value"}""")
        )
        advanceUntilIdle()
        val request = mockEngine.requestHistory.first()

        assertEquals(mapOf("key" to listOf("value")), request.url.parameters.toMap())
        assertEquals("Header", request.headers["Random"])
        assertEquals("""{"key":"value"}""", request.body.toByteArray().decodeToString())
        assertEquals(200, response.statusCode)
    }
}

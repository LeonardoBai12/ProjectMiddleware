package io.lb.middleware.data.repository

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareAuthHeader
import io.lb.common.data.request.MiddlewareAuthHeaderType
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.common.shared.error.MiddlewareException
import io.lb.common.shared.flow.Resource
import io.lb.middleware.data.datasource.MiddlewareDataSource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MiddlewareRepositoryimplTest {
    private lateinit var dataSource: MiddlewareDataSource
    private lateinit var repository: MiddlewareRepositoryImpl

    @BeforeEach
    fun setUp() {
        dataSource = mockk(relaxed = true)
        repository = MiddlewareRepositoryImpl(dataSource)
    }

    @Test
    fun `When startMiddleware is called, expect to call configGenericRoutes and configStoredMappedRoutes`() = runTest {
        val result = repository.startMiddleware().single()

        assert(result is Resource.Success)
        verify { dataSource.configGenericRoutes(any()) }
        coVerify { dataSource.configStoredMappedRoutes() }
    }

    @Test
    fun `When startMiddleware is called and an exception is thrown, expect to return Resource Error`() = runTest {
        coEvery { dataSource.configGenericRoutes(any()) } throws MiddlewareException(400, "Error")

        val result = repository.startMiddleware().single()

        assert(result is Resource.Error)
    }

    @Test
    fun `When configureStoredMappedRoutes is called, expect to call configStoredMappedRoutes`() = runTest {
        coEvery { dataSource.configStoredMappedRoutes() } returns 2

        val result = repository.configureStoredMappedRoutes().single()

        assert(result is Resource.Success)
        assertEquals("Created 2 mapped routes", (result as Resource.Success).data)
        coVerify { dataSource.configStoredMappedRoutes() }
    }

    @Test
    fun `When configureStoredMappedRoutes is called and an exception is thrown, expect to return Error`() = runTest {
        coEvery { dataSource.configStoredMappedRoutes() } throws MiddlewareException(400, "Error")

        val result = repository.configureStoredMappedRoutes().single()

        assert(result is Resource.Error)
    }

    @Test
    fun `When stopMiddleware is called, expect to call stopMiddleware`() = runTest {
        coEvery { dataSource.stopMiddleware() } just Runs

        repository.stopMiddleware()

        coVerify { dataSource.stopMiddleware() }
    }

    @Test
    fun `When createMappedRoute with runtimeAuth and no route authHeader, validation uses runtime auth`() = runTest {
        val capturedLambda = slot<suspend (MappedRoute, MiddlewareAuthHeader?) -> MappedRoute>()
        val capturedValidationRoute = slot<MappedRoute>()

        every { dataSource.configGenericRoutes(capture(capturedLambda)) } just Runs
        coEvery { dataSource.getMappedResponse(capture(capturedValidationRoute)) } returns MappedResponse(200, "{}")
        coEvery { dataSource.createMappedRoute(any()) } returns "new-uuid"

        repository.startMiddleware().collect {}

        val runtimeAuth = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Bearer, "creation-token")
        capturedLambda.captured.invoke(buildMappedRoute(authHeader = null), runtimeAuth)

        assertEquals(runtimeAuth, capturedValidationRoute.captured.originalRoute.authHeader)
    }

    @Test
    fun `When createMappedRoute with runtimeAuth, stored route does not contain runtime auth`() = runTest {
        val capturedLambda = slot<suspend (MappedRoute, MiddlewareAuthHeader?) -> MappedRoute>()
        val capturedStoredRoute = slot<MappedRoute>()

        every { dataSource.configGenericRoutes(capture(capturedLambda)) } just Runs
        coEvery { dataSource.getMappedResponse(any()) } returns MappedResponse(200, "{}")
        coEvery { dataSource.createMappedRoute(capture(capturedStoredRoute)) } returns "new-uuid"

        repository.startMiddleware().collect {}

        val runtimeAuth = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Bearer, "creation-token")
        capturedLambda.captured.invoke(buildMappedRoute(authHeader = null), runtimeAuth)

        assertNull(capturedStoredRoute.captured.originalRoute.authHeader)
    }

    @Test
    fun `When createMappedRoute with pre-configured auth and runtimeAuth, pre-configured auth used for validation`() = runTest {
        val capturedLambda = slot<suspend (MappedRoute, MiddlewareAuthHeader?) -> MappedRoute>()
        val capturedValidationRoute = slot<MappedRoute>()

        every { dataSource.configGenericRoutes(capture(capturedLambda)) } just Runs
        coEvery { dataSource.getMappedResponse(capture(capturedValidationRoute)) } returns MappedResponse(200, "{}")
        coEvery { dataSource.createMappedRoute(any()) } returns "new-uuid"

        repository.startMiddleware().collect {}

        val preConfiguredAuth = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Basic, "stored-token")
        val runtimeAuth = MiddlewareAuthHeader(MiddlewareAuthHeaderType.Bearer, "runtime-token")
        capturedLambda.captured.invoke(buildMappedRoute(authHeader = preConfiguredAuth), runtimeAuth)

        assertEquals(preConfiguredAuth, capturedValidationRoute.captured.originalRoute.authHeader)
    }

    @Test
    fun `When createMappedRoute with no runtimeAuth and no authHeader, validation receives null auth`() = runTest {
        val capturedLambda = slot<suspend (MappedRoute, MiddlewareAuthHeader?) -> MappedRoute>()
        val capturedValidationRoute = slot<MappedRoute>()

        every { dataSource.configGenericRoutes(capture(capturedLambda)) } just Runs
        coEvery { dataSource.getMappedResponse(capture(capturedValidationRoute)) } returns MappedResponse(200, "{}")
        coEvery { dataSource.createMappedRoute(any()) } returns "new-uuid"

        repository.startMiddleware().collect {}

        capturedLambda.captured.invoke(buildMappedRoute(authHeader = null), null)

        assertNull(capturedValidationRoute.captured.originalRoute.authHeader)
    }

    @Test
    fun `When createMappedRoute succeeds, expect configureMappedRoute called with uuid path`() = runTest {
        val capturedLambda = slot<suspend (MappedRoute, MiddlewareAuthHeader?) -> MappedRoute>()
        val capturedConfiguredRoute = slot<MappedRoute>()

        every { dataSource.configGenericRoutes(capture(capturedLambda)) } just Runs
        coEvery { dataSource.getMappedResponse(any()) } returns MappedResponse(200, "{}")
        coEvery { dataSource.createMappedRoute(any()) } returns "generated-uuid"
        coEvery { dataSource.configureMappedRoute(capture(capturedConfiguredRoute)) } just Runs

        repository.startMiddleware().collect {}

        val route = buildMappedRoute(authHeader = null)
        capturedLambda.captured.invoke(route, null)

        coVerify { dataSource.configureMappedRoute(any()) }
        assertEquals("v1/generated-uuid/${route.path}", capturedConfiguredRoute.captured.path)
    }

    private fun buildMappedRoute(authHeader: MiddlewareAuthHeader?) = MappedRoute(
        path = "test-path",
        method = MiddlewareHttpMethods.Get,
        mappedApi = MappedApi(
            uuid = "api-uuid",
            originalApi = OriginalApi(baseUrl = "https://example.com/")
        ),
        originalRoute = OriginalRoute(
            path = "original-path",
            method = MiddlewareHttpMethods.Get,
            originalApi = OriginalApi(baseUrl = "https://example.com/"),
            authHeader = authHeader
        ),
        rulesAsString = "{}"
    )
}

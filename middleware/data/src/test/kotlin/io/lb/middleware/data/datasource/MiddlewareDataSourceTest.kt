package io.lb.middleware.data.datasource

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.common.data.service.ClientService
import io.lb.common.data.service.DatabaseService
import io.lb.common.data.service.MapperService
import io.lb.common.data.service.ServerService
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MiddlewareDataSourceTest {
    @MockK
    private lateinit var clientService: ClientService

    @MockK
    private lateinit var databaseService: DatabaseService

    @MockK
    private lateinit var serverService: ServerService

    @MockK
    private lateinit var mapperService: MapperService

    private lateinit var dataSource: MiddlewareDataSource

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = MiddlewareDataSource(
            clientService,
            databaseService,
            serverService,
            mapperService
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When configGenericRoutes is called, expect startGenericMappingRoute and startPreviewRoute`() = runTest {
        every { serverService.startGenericMappingRoute(any()) } just Runs
        every { serverService.startPreviewRoute(any()) } just Runs

        dataSource.configGenericRoutes()

        verify { serverService.startGenericMappingRoute(any()) }
        verify { serverService.startPreviewRoute(any()) }
    }

    @Test
    fun `When configStoredMappedRoutes is called, expect createMappedRoutes and queryAllMappedRoutes`() = runTest {
        val mockRoutes = listOf(mockk<MappedRoute>())
        coEvery { databaseService.queryAllMappedRoutes() } returns mockRoutes
        coEvery { serverService.createMappedRoutes(any(), any()) } just Runs

        val result = dataSource.configStoredMappedRoutes()

        assertEquals(result, mockRoutes.size)
        coVerify { databaseService.queryAllMappedRoutes() }
        coVerify { serverService.createMappedRoutes(any(), any()) }
    }

    @Test
    fun `When createMappedRoute is called, expect createMappedRoute and configureMappedRoute`() = runTest {
        val originalRoute = OriginalRoute(
            path = "path",
            method = MiddlewareHttpMethods.Post,
            originalApi = OriginalApi(
                baseUrl = "baseUrl"
            ),
        )
        val mappedRoute = MappedRoute(
            path = "path",
            mappedApi = mockk(),
            originalRoute = originalRoute,
            method = MiddlewareHttpMethods.Post,
            rulesAsString = "rules"
        )
        coEvery { databaseService.createMappedRoute(any()) } just Runs
        coEvery { serverService.createMappedRoute(any(), any()) } just Runs

        dataSource.createMappedRoute(mappedRoute)

        coVerify { databaseService.createMappedRoute(mappedRoute) }
        coVerify { serverService.createMappedRoute(mappedRoute, any()) }
    }

    @Test
    fun `When configureMappedRoute is called, expect createMappedRoute`() = runTest {
        val originalResponse = OriginalResponse(
            statusCode = 200,
            body = "body",
        )
        val mappedResponse = MappedResponse(
            statusCode = 200,
            body = "body",
        )
        val originalRoute = OriginalRoute(
            path = "path",
            method = MiddlewareHttpMethods.Post,
            originalApi = OriginalApi(
                baseUrl = "baseUrl"
            ),
        )
        val mappedRoute = MappedRoute(
            path = "path",
            mappedApi = mockk(),
            originalRoute = originalRoute,
            method = MiddlewareHttpMethods.Post,
            rulesAsString = "rules"
        )
        coEvery { clientService.request(any(), any(), any(), any()) } returns originalResponse
        coEvery { mapperService.mapResponse(any(), any()) } returns mappedResponse

        dataSource.getMappedResponse(mappedRoute)

        coVerify { clientService.request(originalRoute, any(), any(), any()) }
        coVerify { mapperService.mapResponse(any(), originalResponse) }
    }
}

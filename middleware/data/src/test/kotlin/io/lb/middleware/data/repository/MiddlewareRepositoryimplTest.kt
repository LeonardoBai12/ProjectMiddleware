package io.lb.middleware.data.repository

import io.lb.common.shared.error.MiddlewareException
import io.lb.common.shared.flow.Resource
import io.lb.middleware.data.datasource.MiddlewareDataSource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
        verify { dataSource.configGenericRoutes() }
        coVerify { dataSource.configStoredMappedRoutes() }
    }

    @Test
    fun `When startMiddleware is called and an exception is thrown, expect to return Resource Error`() = runTest {
        coEvery { dataSource.configGenericRoutes() } throws MiddlewareException(400, "Error")

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
}

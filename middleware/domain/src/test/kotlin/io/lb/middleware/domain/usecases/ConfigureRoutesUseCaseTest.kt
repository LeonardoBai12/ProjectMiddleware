package io.lb.middleware.domain.usecases

import io.lb.common.shared.flow.Resource
import io.lb.middleware.domain.repository.MiddlewareRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConfigureRoutesUseCaseTest {
    @MockK
    private lateinit var repository: MiddlewareRepository

    private lateinit var configureRoutesUseCase: ConfigureRoutesUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        configureRoutesUseCase = ConfigureRoutesUseCase(repository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When invoke is called, expect configureStoredMappedRoutes from repository is called`() = runTest {
        every { repository.configureStoredMappedRoutes() } returns flowOf(Resource.Success("Success"))

        val result = configureRoutesUseCase().single()

        assert(result is Resource.Success)
        verify { repository.configureStoredMappedRoutes() }
    }
}

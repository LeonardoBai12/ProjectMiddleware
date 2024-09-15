package io.lb.middleware.domain.usecases

import io.lb.middleware.domain.repository.MiddlewareRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StopMiddlewareUseCaseTest {
    @MockK
    private lateinit var repository: MiddlewareRepository

    private lateinit var useCase: StopMiddlewareUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = StopMiddlewareUseCase(repository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When invoke is called, expect stopMiddleware is called`() = runTest {
        coEvery { repository.stopMiddleware() } returns Unit

        useCase()

        coVerify { repository.stopMiddleware() }
    }
}

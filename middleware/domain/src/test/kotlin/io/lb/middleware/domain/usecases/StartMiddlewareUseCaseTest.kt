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

class StartMiddlewareUseCaseTest {
    @MockK
    private lateinit var middlewareRepository: MiddlewareRepository

    private lateinit var startMiddlewareUseCase: StartMiddlewareUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startMiddlewareUseCase = StartMiddlewareUseCase(middlewareRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When startMiddleware is called, expect startMiddleware from repository is called`() = runTest {
        every { middlewareRepository.startMiddleware() } returns flowOf(Resource.Success(Unit))

        val result = startMiddlewareUseCase().single()

        assert(result is Resource.Success)
        verify { middlewareRepository.startMiddleware() }
    }
}

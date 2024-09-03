package io.lb.middleware.domain.controller

import io.lb.common.shared.flow.Resource
import io.lb.middleware.domain.model.MiddlewareEvent
import io.lb.middleware.domain.model.MiddlewareState
import io.lb.middleware.domain.usecases.ConfigureRoutesUseCase
import io.lb.middleware.domain.usecases.StartMiddlewareUseCase
import io.lb.middleware.domain.usecases.StopMiddlewareUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MiddlewareControllerTest {
    @MockK
    private lateinit var configureRoutesUseCase: ConfigureRoutesUseCase

    @MockK
    private lateinit var startMiddlewareUseCase: StartMiddlewareUseCase

    @MockK
    private lateinit var stopMiddlewareUseCase: StopMiddlewareUseCase

    private lateinit var middlewareController: MiddlewareController

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        middlewareController = MiddlewareController(
            CoroutineScope(UnconfinedTestDispatcher()),
            startMiddlewareUseCase,
            configureRoutesUseCase,
            stopMiddlewareUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When onEvent is called with StartMiddleware event, expect startMiddleware is called`() = runTest {
        every { startMiddlewareUseCase() } returns flowOf(Resource.Success(Unit))
        every { configureRoutesUseCase() } returns flowOf(Resource.Success("Success"))

        val initialState = middlewareController.state.value
        assert(initialState is MiddlewareState.Idle)

        middlewareController.onEvent(MiddlewareEvent.StartMiddleware)
        advanceUntilIdle()

        val finalState = middlewareController.state.value
        assert(finalState is MiddlewareState.Running)

        verify { startMiddlewareUseCase() }
        verify { configureRoutesUseCase() }
    }

    @Test
    fun `When start and configureRoutesUseCase returns an error, expect server to stop`() = runTest {
        every { startMiddlewareUseCase() } returns flowOf(Resource.Success(Unit))
        every { configureRoutesUseCase() } returns flowOf(Resource.Error("Error"))
        every { stopMiddlewareUseCase() } just runs

        val initialState = middlewareController.state.value
        assert(initialState is MiddlewareState.Idle)

        middlewareController.onEvent(MiddlewareEvent.StartMiddleware)

        val finalState = middlewareController.state.value
        assert(finalState is MiddlewareState.Stopped)
        verify { startMiddlewareUseCase() }
    }

    @Test
    fun `When start and startMiddlewareUseCase returns an error, expect server to stop`() = runTest {
        every { startMiddlewareUseCase() } returns flowOf(Resource.Error("Error"))
        every { stopMiddlewareUseCase() } just runs

        val initialState = middlewareController.state.value
        assert(initialState is MiddlewareState.Idle)

        middlewareController.onEvent(MiddlewareEvent.StartMiddleware)

        val finalState = middlewareController.state.value
        assert(finalState is MiddlewareState.Stopped)
        verify { startMiddlewareUseCase() }
        verify { stopMiddlewareUseCase() }
    }
}

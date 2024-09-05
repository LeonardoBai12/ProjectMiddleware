package io.lb.middleware.core

import io.lb.middleware.domain.controller.MiddlewareController
import io.lb.middleware.domain.model.MiddlewareEvent
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MiddlewareTest {
    private lateinit var controller: MiddlewareController
    private lateinit var middleware: Middleware

    @BeforeEach
    fun setUp() {
        controller = mockk(relaxed = true)
        middleware = Middleware(
            CoroutineScope(UnconfinedTestDispatcher()),
            controller
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When startMiddleware is called, expect controller state is collected`() = runTest {
        every { controller.onEvent(any()) } just runs

        middleware.startMiddleware()
        advanceUntilIdle()

        verify { controller.onEvent(MiddlewareEvent.StartMiddleware) }
    }
}

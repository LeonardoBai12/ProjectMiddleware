package io.lb.middleware.core

import io.lb.middleware.domain.controller.MiddlewareController
import io.lb.middleware.domain.model.MiddlewareEvent
import io.lb.middleware.domain.model.MiddlewareState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

/**
 * Middleware class that starts the middleware.
 */
class Middleware(
    coroutineScope: CoroutineScope,
    private val controller: MiddlewareController
) {
    init {
        coroutineScope.launch {
            startMiddleware()
        }
    }

    /**
     * Starts the middleware.
     */
    fun start() {
        controller.onEvent(MiddlewareEvent.StartMiddleware)
    }

    private suspend fun startMiddleware() {
        controller.state.collectLatest {
            when (it) {
                is MiddlewareState.Idle -> {
                    println("Middleware state: Idle")
                }
                is MiddlewareState.Running -> {
                    println("Middleware state: Running")
                }
                is MiddlewareState.Stopped -> {
                    println("Middleware state: Stopped")
                }
            }
        }
    }
}

package io.lb.middleware.domain.model

sealed class MiddlewareState {
    data object Idle : MiddlewareState()
    data object Running : MiddlewareState()
    data object Stopped : MiddlewareState()
}

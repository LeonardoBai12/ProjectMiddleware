package io.lb.middleware.domain.model

sealed interface MiddlewareEvent {
    data object StartMiddleware : MiddlewareEvent
}

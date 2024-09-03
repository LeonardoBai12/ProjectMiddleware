package io.lb.middleware.domain.usecases

import io.lb.middleware.domain.repository.MiddlewareRepository

/**
 * StopMiddlewareUseCase is responsible for stopping the middleware.
 *
 * @property repository Repository for the middleware.
 */
class StopMiddlewareUseCase(
    private val repository: MiddlewareRepository
) {
    /**
     * Stops the middleware.
     */
    operator fun invoke() = repository.stopMiddleware()
}

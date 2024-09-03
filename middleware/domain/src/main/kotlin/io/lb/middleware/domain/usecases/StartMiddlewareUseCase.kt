package io.lb.middleware.domain.usecases

import io.lb.middleware.domain.repository.MiddlewareRepository

/**
 * StartMiddlewareUseCase is responsible for starting the middleware.
 *
 * @property repository Repository for the middleware.
 */
class StartMiddlewareUseCase(
    private val repository: MiddlewareRepository
) {
    /**
     * Starts the middleware.
     */
    operator fun invoke() = repository.startMiddleware()
}

package io.lb.middleware.domain.usecases

import io.lb.middleware.domain.repository.MiddlewareRepository

/**
 * ConfigureRoutesUseCase is responsible for configuring the stored mapped routes.
 *
 * @property repository Repository for the middleware.
 */
class ConfigureRoutesUseCase(
    private val repository: MiddlewareRepository
) {
    /**
     * Configures the stored mapped routes.
     */
    operator fun invoke() = repository.configureStoredMappedRoutes()
}

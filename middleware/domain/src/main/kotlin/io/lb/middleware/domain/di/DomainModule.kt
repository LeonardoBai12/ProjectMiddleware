package io.lb.middleware.domain.di

import io.lb.middleware.domain.controller.MiddlewareController
import io.lb.middleware.domain.repository.MiddlewareRepository
import io.lb.middleware.domain.usecases.ConfigureRoutesUseCase
import io.lb.middleware.domain.usecases.StartMiddlewareUseCase
import io.lb.middleware.domain.usecases.StopMiddlewareUseCase
import kotlinx.coroutines.CoroutineScope

fun provideMiddlewareController(
    coroutineScope: CoroutineScope,
    repository: MiddlewareRepository
): MiddlewareController {
    return MiddlewareController(
        coroutineScope = coroutineScope,
        startMiddlewareUseCase = StartMiddlewareUseCase(repository),
        configureRoutesUseCase = ConfigureRoutesUseCase(repository),
        stopMiddlewareUseCase = StopMiddlewareUseCase(repository)
    )
}

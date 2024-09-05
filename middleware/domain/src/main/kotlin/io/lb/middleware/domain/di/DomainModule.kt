package io.lb.middleware.domain.di

import io.lb.middleware.domain.controller.MiddlewareController
import io.lb.middleware.domain.usecases.ConfigureRoutesUseCase
import io.lb.middleware.domain.usecases.StartMiddlewareUseCase
import io.lb.middleware.domain.usecases.StopMiddlewareUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { ConfigureRoutesUseCase(get()) }
    factory { StartMiddlewareUseCase(get()) }
    factory { StopMiddlewareUseCase(get()) }
    single { MiddlewareController(get(), get(), get(), get()) }
}

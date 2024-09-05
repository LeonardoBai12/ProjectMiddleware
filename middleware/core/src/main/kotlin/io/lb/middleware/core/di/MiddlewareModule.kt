package io.lb.middleware.core.di

import io.lb.middleware.core.Middleware
import org.koin.dsl.module

val middlewareModule = module {
    single { Middleware(get(), get()) }
}

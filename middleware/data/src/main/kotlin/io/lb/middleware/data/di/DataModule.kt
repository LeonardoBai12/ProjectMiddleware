package io.lb.middleware.data.di

import io.lb.middleware.data.datasource.MiddlewareDataSource
import io.lb.middleware.data.repository.MiddlewareRepositoryImpl
import io.lb.middleware.domain.repository.MiddlewareRepository
import org.koin.dsl.module

val dataModule = module {
    factory { MiddlewareDataSource(get(), get(), get(), get()) }
    single<MiddlewareRepository> { MiddlewareRepositoryImpl(get()) }
}

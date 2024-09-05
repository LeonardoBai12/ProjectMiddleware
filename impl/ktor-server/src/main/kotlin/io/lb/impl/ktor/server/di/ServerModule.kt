package io.lb.impl.ktor.server.di

import io.lb.common.data.service.ServerService
import io.lb.impl.ktor.server.service.ServerServiceImpl
import org.koin.dsl.module

val serverModule = module {
    single<ServerService> { ServerServiceImpl(get()) }
}

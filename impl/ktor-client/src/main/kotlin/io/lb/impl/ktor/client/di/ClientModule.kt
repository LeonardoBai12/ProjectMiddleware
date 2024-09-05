package io.lb.impl.ktor.client.di

import io.ktor.client.HttpClient
import io.lb.common.data.service.ClientService
import io.lb.impl.ktor.client.MiddlewareClient
import io.lb.impl.ktor.client.service.ClientServiceImpl
import org.koin.dsl.module

val clientModule = module {
    factory<HttpClient> { MiddlewareClient.client }
    single<ClientService> { ClientServiceImpl(get()) }
}

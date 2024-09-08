package io.lb.impl.ktor.client.di

import io.lb.common.data.service.ClientService
import io.lb.impl.ktor.client.MiddlewareClient
import io.lb.impl.ktor.client.service.ClientServiceImpl

private val client = MiddlewareClient.client
fun provideClientService(): ClientService = ClientServiceImpl(client)

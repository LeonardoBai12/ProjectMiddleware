package io.lb.middleware.data.di

import io.lb.common.data.service.ClientService
import io.lb.common.data.service.DatabaseService
import io.lb.common.data.service.MapperService
import io.lb.common.data.service.ServerService
import io.lb.middleware.data.datasource.MiddlewareDataSource
import io.lb.middleware.data.repository.MiddlewareRepositoryImpl
import io.lb.middleware.domain.repository.MiddlewareRepository

private fun provideMiddlewareDataSource(
    clientService: ClientService,
    databaseService: DatabaseService,
    serverService: ServerService,
    mapperService: MapperService,
): MiddlewareDataSource = MiddlewareDataSource(
    clientService,
    databaseService,
    serverService,
    mapperService,
)

fun provideMiddlewareRepository(
    clientService: ClientService,
    databaseService: DatabaseService,
    serverService: ServerService,
    mapperService: MapperService,
): MiddlewareRepository = MiddlewareRepositoryImpl(
    provideMiddlewareDataSource(
        clientService,
        databaseService,
        serverService,
        mapperService,
    )
)

package io.lb.middleware.core.di

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.lb.impl.ktor.client.di.provideClientService
import io.lb.impl.ktor.server.di.provideServerService
import io.lb.impl.ktor.server.plugins.configureAuth
import io.lb.impl.ktor.server.plugins.configureMonitoring
import io.lb.impl.ktor.server.plugins.configureSerialization
import io.lb.impl.ktor.server.plugins.configureSession
import io.lb.impl.mongo.database.di.provideDatabaseService
import io.lb.middleware.core.Middleware
import io.lb.middleware.data.di.provideMiddlewareRepository
import io.lb.middleware.domain.di.provideMiddlewareController
import io.lb.middleware.mapper.di.provideMapperService
import kotlinx.coroutines.CoroutineScope

internal fun provideMiddleware(
    coroutineScope: CoroutineScope,
    engine: NettyApplicationEngine
): Middleware {
    val repository = provideMiddlewareRepository(
        clientService = provideClientService(),
        databaseService = provideDatabaseService(),
        serverService = provideServerService(engine),
        mapperService = provideMapperService(),
    )

    val controller = provideMiddlewareController(
        coroutineScope = coroutineScope,
        repository = repository
    )

    return Middleware(
        coroutineScope = coroutineScope,
        controller = controller
    )
}

/**
 * Main function of the server.
 */
internal fun configureServer(): NettyApplicationEngine {
    return embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    )
}

/**
 * Application module configuration.
 */
private fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureSession()
    configureAuth()
}

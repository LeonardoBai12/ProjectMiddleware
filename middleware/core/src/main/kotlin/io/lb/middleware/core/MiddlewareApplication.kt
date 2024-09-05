package io.lb.middleware.core

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.lb.impl.ktor.server.plugins.configureAuth
import io.lb.impl.ktor.server.plugins.configureInjection
import io.lb.impl.ktor.server.plugins.configureMonitoring
import io.lb.impl.ktor.server.plugins.configureSerialization
import io.lb.impl.ktor.server.plugins.configureSession
import org.koin.core.module.Module

fun main() {
    configureAndRunServer(emptyList())
}

/**
 * Main function of the server.
 */
fun configureAndRunServer(injectionModules: List<Module>): NettyApplicationEngine {
    return embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = { module(injectionModules) }
    ).start(wait = true)
}

/**
 * Application module configuration.
 */
internal fun Application.module(injectionModules: List<Module>) {
    configureInjection(injectionModules)
    configureSerialization()
    configureMonitoring()
    configureSession()
    configureAuth()
}

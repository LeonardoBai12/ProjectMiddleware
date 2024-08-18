package io.lb.impl.ktor.server

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.lb.impl.ktor.server.plugins.configureAuth
import io.lb.impl.ktor.server.plugins.configureMonitoring
import io.lb.impl.ktor.server.plugins.configureSerialization
import io.lb.impl.ktor.server.plugins.configureSession

/**
 * Main function of the server.
 */
fun configureAndRunServer(): NettyApplicationEngine {
    return embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

/**
 * Application module configuration.
 */
internal fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureSession()
    configureAuth()
}

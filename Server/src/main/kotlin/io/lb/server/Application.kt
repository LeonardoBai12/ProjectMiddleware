package io.lb.server

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.lb.server.plugins.configureAuth
import io.lb.server.plugins.configureMonitoring
import io.lb.server.plugins.configureSerialization
import io.lb.server.plugins.configureSession

/**
 * Main function of the server.
 */
fun Application.configureServe() {
    embeddedServer(
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





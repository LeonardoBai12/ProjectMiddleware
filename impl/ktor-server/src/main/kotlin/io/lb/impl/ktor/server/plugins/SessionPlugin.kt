package io.lb.impl.ktor.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.lb.impl.ktor.server.model.MiddlewareSession

private const val SESSION_NAME = "WareHouseSessions"

/**
 * Configure the session plugin.
 */
fun Application.configureSession() {
    install(Sessions) {
        cookie<MiddlewareSession>(SESSION_NAME)
    }
}

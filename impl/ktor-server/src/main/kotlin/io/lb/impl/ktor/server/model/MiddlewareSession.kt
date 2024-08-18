package io.lb.impl.ktor.server.model

/**
 * Data class containing the client ID and session ID of a middleware session.
 *
 * @property clientId The client ID of the middleware session.
 * @property sessionId The session ID of the middleware session.
 */
data class MiddlewareSession(
    val clientId: String,
    val sessionId: String,
)

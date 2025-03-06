package io.lb.impl.ktor.server.model

data class MiddlewareUserParameters(
    val secret: String,
    val audience: String,
    val issuer: String,
    val userId: String,
    val email: String,
    val expiration: Long,
)

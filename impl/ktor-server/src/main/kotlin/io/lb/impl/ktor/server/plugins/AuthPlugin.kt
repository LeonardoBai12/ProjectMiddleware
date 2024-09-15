package io.lb.impl.ktor.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.lb.impl.ktor.server.model.TokenConfig

/**
 * Configure the authentication plugin.
 */
fun Application.configureAuth() {
    val config = TokenConfig.middlewareTokenConfig(
        config = environment.config,
        embedded = false
    )

    authentication {
        jwt {
            realm = "Middleware"
            verifier(
                JWT.require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

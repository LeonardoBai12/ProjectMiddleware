package io.lb.impl.ktor.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.lb.impl.ktor.server.model.MiddlewareUserParameters
import io.lb.impl.ktor.server.model.TokenConfig

/**
 * Configure the authentication plugin.
 */
fun Application.configureAuth(
    onValidateUser: suspend (MiddlewareUserParameters) -> Boolean
) {
    val config = TokenConfig.middlewareTokenConfig(
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
                val userId = credential.payload.getClaim("userId").asString()
                val email = credential.payload.getClaim("email").asString()
                val expiration = credential.payload.expiresAt.time

                if (credential.payload.audience.contains(config.audience) &&
                    onValidateUser(
                        MiddlewareUserParameters(
                            secret = config.secret,
                            audience = config.audience,
                            issuer = config.issuer,
                            userId = userId,
                            email = email,
                            expiration = expiration
                        )
                    )
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

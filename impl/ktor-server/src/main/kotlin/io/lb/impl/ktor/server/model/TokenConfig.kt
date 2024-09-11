package io.lb.impl.ktor.server.model

import io.ktor.server.config.ApplicationConfig
import java.io.FileInputStream
import java.util.Properties

/**
 * Data class representing token configurations.
 *
 * @param issuer Domain of the tokens to be generated.
 * @param audience Audience of the tokens to be generated.
 * @param expiresIn Timestamp in millis of the experitation date of the token.
 * @param secret Secret key of the token configuration.
 */
internal data class TokenConfig(
    val issuer: String,
    val audience: String,
    val expiresIn: Long,
    val secret: String
) {
    companion object {
        /**
         * Default TokenConfig for the WareHouse project.
         *
         * @param config Represents an application config node.
         * @param embedded Represents whether the server is embbeded.
         *
         * @return A TokenConfig instance with the default values for the WareHouse project.
         */
        fun middlewareTokenConfig(
            config: ApplicationConfig,
            embedded: Boolean
        ): TokenConfig {
            val secret: String = if (embedded) {
                System.getenv("SECRET_KEY")
            } else {
                val properties = Properties()
                properties.load(FileInputStream(config.property("local.properties").getString()))
                properties.getProperty("jwt.secret_key")
            }
            return TokenConfig(
                issuer = "http://projectmiddleware.fly.dev:8080",
                audience = "users",
                expiresIn = 365L * 1000L * 60L * 60L * 24L,
                secret = secret
            )
        }
    }
}

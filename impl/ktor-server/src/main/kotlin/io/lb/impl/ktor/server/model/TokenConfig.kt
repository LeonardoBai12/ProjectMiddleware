package io.lb.impl.ktor.server.model

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
         * @param embedded Represents whether the server is embbeded.
         *
         * @return A TokenConfig instance with the default values for the WareHouse project.
         */
        fun middlewareTokenConfig(
            embedded: Boolean
        ): TokenConfig {
            val secret: String = if (embedded) {
                val properties = Properties()
                val fileInputStream = FileInputStream("local.properties")
                properties.load(fileInputStream)
                properties.getProperty("jwt.secret_key")
            } else {
                System.getenv("SECRET_KEY")
            }
            return TokenConfig(
                issuer = "https://projectmiddleware.fly.dev:8080",
                audience = "users",
                expiresIn = 365L * 1000L * 60L * 60L * 24L,
                secret = secret
            )
        }
    }
}

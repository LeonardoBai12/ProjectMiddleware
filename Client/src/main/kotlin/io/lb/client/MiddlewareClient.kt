package io.lb.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Object containing the middleware client.
 */
internal object MiddlewareClient {
    private const val MAX_RETRIES = 5
    private const val RETRY_DELAY = 3000L

    /**
     * The middleware client.
     */
    val client = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(HttpRequestRetry) {
            maxRetries = MAX_RETRIES
            retryIf { _, response ->
                response.status.isSuccess().not() &&
                    response.status != HttpStatusCode.Unauthorized &&
                    response.status != HttpStatusCode.NotFound
            }
            delayMillis { retry ->
                retry * RETRY_DELAY
            }
        }
    }
}

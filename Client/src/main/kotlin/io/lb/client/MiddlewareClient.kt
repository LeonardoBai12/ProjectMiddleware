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

internal object MiddlewareClient {
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
            maxRetries = 5
            retryIf { _, response ->
                response.status.isSuccess().not() &&
                    response.status != HttpStatusCode.Unauthorized &&
                    response.status != HttpStatusCode.NotFound
            }
            delayMillis { retry ->
                retry * 3000L
            }
        }
    }
}

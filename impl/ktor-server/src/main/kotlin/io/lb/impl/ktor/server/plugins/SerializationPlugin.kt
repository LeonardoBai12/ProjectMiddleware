package io.lb.impl.ktor.server.plugins

import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

/**
 * Configure the serialization plugin.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
        gson {
        }
    }
}

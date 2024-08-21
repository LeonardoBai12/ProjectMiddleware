package io.lb.impl.ktor.server.util

import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.ApplicationTestBuilder

fun ApplicationTestBuilder.setupApplication() {
    install(ContentNegotiation) {
        json()
        gson {
        }
    }
}

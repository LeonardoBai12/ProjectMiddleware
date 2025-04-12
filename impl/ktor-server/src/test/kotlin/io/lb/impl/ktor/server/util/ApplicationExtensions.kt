package io.lb.impl.ktor.server.util

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basic
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.util.generateNonce
import io.lb.impl.ktor.server.model.MiddlewareSession
import kotlinx.serialization.json.Json

fun ApplicationTestBuilder.setupApplication(block: Application.() -> Unit) {
    install(ContentNegotiation) {
        json()
        gson {
        }
    }

    application {
        setupApplication()
    }
}


fun Application.setupApplication() {
    configureAuth()
}

fun Application.configureAuth() {
    authentication {
        basic {
            validate {
                UserIdPrincipal("UserName")
            }
        }
    }
}

fun Application.configureSession(
    bypass: Boolean = true,
    userId: String = ""
) {
    install(Sessions) {
        cookie<MiddlewareSession>("WareHouse-Test")
    }

    if (!bypass) return

    intercept(ApplicationCallPipeline.Call) {
        call.sessions.get<MiddlewareSession>() ?: run {
            val clientId = userId.ifBlank { call.parameters["userId"] ?: "" }

            call.sessions.set(
                MiddlewareSession(
                    clientId = clientId,
                    sessionId = generateNonce()
                )
            )
        }
    }
}

fun HttpRequestBuilder.setupRequest() {
    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    basicAuth("basic", "auth")
}
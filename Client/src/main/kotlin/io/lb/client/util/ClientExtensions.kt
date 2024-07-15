package io.lb.client.util

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import io.lb.data.model.OriginalResponse
import io.lb.data.model.OriginalRoute

internal suspend fun HttpClient.request(route: OriginalRoute): OriginalResponse {
    val response = request {
        method = HttpMethod.parse(route.method.name)

        url(route.originalApi.baseUrl) {
            path(route.path)
            route.queries.forEach {
                parameters.append(it.key, it.value)
            }
        }

        route.authHeader?.fullToken()?.let {
            headers.append(HttpHeaders.Authorization, it)
        }

        route.headers.forEach {
            headers.append(it.key, it.value)
        }

        if (headers.contains(HttpHeaders.ContentType).not()) {
            contentType(ContentType.Application.Json)
        }
        setBody(route.body)
    }

    return OriginalResponse(
        statusCode = response.status.value,
        body = response.bodyAsText()
    )
}

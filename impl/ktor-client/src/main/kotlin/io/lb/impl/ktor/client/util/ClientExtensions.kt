package io.lb.impl.ktor.client.util

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute

/**
 * Extension function to make a request to the API.
 *
 * @throws IllegalArgumentException If the base URL does not start with "https://".
 *
 * @param route The route to make the request to.
 * @param queries The queries to pass to the API.
 * @return The response from the API.
 */
internal suspend fun HttpClient.request(
    route: OriginalRoute,
    queries: Map<String, String>
): OriginalResponse {
    val response = request {
        method = HttpMethod.parse(route.method.name)

        require(route.originalApi.baseUrl.startsWith("https://"))

        url(route.originalApi.baseUrl) {
            protocol = URLProtocol.HTTPS
            path(route.path)
            queries.forEach {
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

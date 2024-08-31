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
 * @param originalRoute The route to make the request to.
 * @param preConfiguredQueries The queries to pass to the API.
 * @param preConfiguredHeaders The headers to pass to the API.
 * @param preConfiguredBody The body to pass to the API.
 * @return The response from the API.
 */
internal suspend fun HttpClient.request(
    originalRoute: OriginalRoute,
    preConfiguredQueries: Map<String, String>,
    preConfiguredHeaders: Map<String, String>,
    preConfiguredBody: String?
): OriginalResponse {
    val response = request {
        method = HttpMethod.parse(originalRoute.method.name)

        require(originalRoute.originalApi.baseUrl.startsWith("https://"))

        url(originalRoute.originalApi.baseUrl) {
            protocol = URLProtocol.HTTPS
            path(originalRoute.path)
            preConfiguredQueries.forEach {
                parameters.append(it.key, it.value)
            }
        }

        originalRoute.authHeader?.fullToken()?.let {
            headers.append(HttpHeaders.Authorization, it)
        }

        preConfiguredHeaders.ifEmpty { originalRoute.headers }
            .forEach { headers.append(it.key, it.value) }

        if (headers.contains(HttpHeaders.ContentType).not()) {
            contentType(ContentType.Application.Json)
        }
        preConfiguredBody?.takeIf { it.isNotBlank() }?.let {
            setBody(it)
        } ?: originalRoute.body?.takeIf { it.isNotBlank() }?.let {
            setBody(it)
        }
    }

    return OriginalResponse(
        statusCode = response.status.value,
        body = response.bodyAsText()
    )
}

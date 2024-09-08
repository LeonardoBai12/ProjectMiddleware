package io.lb.impl.ktor.client.util

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
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
import io.ktor.http.takeFrom
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareHttpMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

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
): OriginalResponse = withContext(Dispatchers.IO) {
    val response = request {
        method = when (originalRoute.method) {
            MiddlewareHttpMethods.Get -> HttpMethod.Get
            MiddlewareHttpMethods.Post -> HttpMethod.Post
            MiddlewareHttpMethods.Head -> HttpMethod.Put
            MiddlewareHttpMethods.Delete -> HttpMethod.Delete
            MiddlewareHttpMethods.Patch -> HttpMethod.Patch
            MiddlewareHttpMethods.Put -> HttpMethod.Put
        }

        require(originalRoute.originalApi.baseUrl.startsWith("https://"))

        originalRoute.authHeader?.fullToken()?.let {
            headers[HttpHeaders.Authorization] = it
        }
        preConfiguredHeaders
            .ifEmpty { originalRoute.headers }
            .forEach {
                if (it.key == HttpHeaders.ContentType) {
                    accept(ContentType.parse(it.value))
                    contentType(ContentType.parse(it.value))
                } else {
                    headers[it.key] = it.value
                }
            }

        preConfiguredBody?.takeIf { it.isNotBlank() }?.let {
            setBody(it)
        } ?: originalRoute.body?.takeIf { it.isNotBlank() }?.let {
            setBody(it)
        }

        url {
            takeFrom(originalRoute.originalApi.baseUrl)

            path(originalRoute.path)
            if (preConfiguredQueries.isNotEmpty()) {
                preConfiguredQueries.forEach {
                    parameters.append(it.key, it.value)
                }
            } else if (originalRoute.queries.isNotEmpty()) {
                originalRoute.queries.forEach {
                    parameters.append(it.key, it.value)
                }
            }
        }
    }

    OriginalResponse(
        statusCode = response.status.value,
        body = response.bodyAsText()
    )
}

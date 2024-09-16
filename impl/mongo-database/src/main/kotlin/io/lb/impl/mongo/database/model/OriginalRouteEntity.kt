package io.lb.impl.mongo.database.model

import io.lb.common.data.model.OriginalApi
import io.lb.common.data.request.MiddlewareAuthHeader
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.impl.mongo.database.DatabaseClient.json
import kotlinx.serialization.json.jsonObject

data class OriginalRouteEntity(
    val path: String,
    val originalApi: OriginalApi,
    val method: MiddlewareHttpMethods,
    val authHeader: MiddlewareAuthHeader? = null,
    val queries: Map<String, String> = mapOf(),
    val headers: Map<String, String> = mapOf(),
    val body: String? = null
) {
    fun toOriginalRoute() = io.lb.common.data.model.OriginalRoute(
        path = path,
        originalApi = originalApi,
        method = method,
        authHeader = authHeader,
        queries = queries,
        headers = headers,
        body = json.parseToJsonElement(body ?: "{}").jsonObject
    )
}

package io.lb.mongo.model

import io.lb.data.model.MappedApi
import io.lb.data.model.MappedRoute
import io.lb.data.model.OriginalRoute
import io.lb.data.util.MiddlewareAuthHeader
import io.lb.data.util.MiddlewareHttpMethods
import java.util.UUID

data class MappedRouteEntity(
    val uuid: UUID,
    val path: String,
    val originalRoute: OriginalRoute,
    val method: MiddlewareHttpMethods,
    val authHeader: MiddlewareAuthHeader? = null,
    val headers: Map<String, String> = mapOf(),
    val query: Map<String, String>?,
    val body: String?
) {
    internal fun toRoute(mappedApi: MappedApi): MappedRoute {
        return MappedRoute(
            uuid = this.uuid,
            path = this.path,
            mappedApi = mappedApi,
            originalRoute = this.originalRoute,
            method = this.method,
            authHeader = this.authHeader,
            headers = this.headers,
            query = this.query,
            body = this.body,
        )
    }
}

internal fun MappedRoute.toEntity(): MappedRouteEntity {
    return MappedRouteEntity(
        uuid = uuid,
        path = path,
        originalRoute = originalRoute,
        method = method,
        authHeader = authHeader,
        headers = headers,
        query = query,
        body = body,
    )
}

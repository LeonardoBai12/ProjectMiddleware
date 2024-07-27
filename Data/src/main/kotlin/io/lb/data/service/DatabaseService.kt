package io.lb.data.service

import io.lb.core.error.MiddlewareException
import io.lb.data.model.MappedApi
import io.lb.data.model.MappedRoute
import kotlin.jvm.Throws

interface DatabaseService {
    suspend fun queryAllMappedRoutes(): List<MappedRoute>
    suspend fun queryMappedApi(apiUuid: String): MappedApi?

    @Throws(MiddlewareException::class)
    suspend fun queryMappedRoutes(apiUuid: String): List<MappedRoute>

    @Throws(MiddlewareException::class)
    suspend fun createMappedRoute(route: MappedRoute)

    @Throws(MiddlewareException::class)
    suspend fun updateMappedRoute(route: MappedRoute)
    suspend fun createMappedApi(api: MappedApi): String

    @Throws(MiddlewareException::class)
    suspend fun updateMappedApi(api: MappedApi)
}

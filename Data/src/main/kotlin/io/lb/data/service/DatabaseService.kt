package io.lb.data.service

import io.lb.data.model.MappedApi
import io.lb.data.model.MappedRoute

interface DatabaseService {
    suspend fun queryAllMappedRoutes(): List<MappedRoute>
    suspend fun queryMappedRoutes(apiUuid: String): List<MappedRoute>
    suspend fun createMappedRoute(route: MappedRoute)
    suspend fun updateMappedRoute(route: MappedRoute)
    suspend fun createMappedApi(api: MappedApi): String
    suspend fun updateMappedApi(api: MappedApi)
}

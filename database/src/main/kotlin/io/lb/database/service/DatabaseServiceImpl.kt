package io.lb.database.service

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.lb.data.model.MappedApi
import io.lb.data.model.MappedRoute
import io.lb.data.service.DatabaseService
import io.lb.database.model.MappedApiEntity
import io.lb.database.model.OriginalApiEntity

class DatabaseServiceImpl(
    private val database: MongoDatabase
) : DatabaseService {
    private val collection = database.getCollection<MappedApiEntity>(MAPPED_API)

    override suspend fun queryAllMappedRoutes(): List<MappedRoute> {
        TODO("Not yet implemented")
    }

    override suspend fun queryMappedRoutes(apiUuid: String): List<MappedRoute> {
        TODO("Not yet implemented")
    }

    override suspend fun createMappedRoute(route: MappedRoute) {
        val apis = database.getCollection<MappedApiEntity>(MAPPED_API)
        TODO("Not yet implemented")
    }

    override suspend fun createMappedApi(api: MappedApi): String {
        val mappedApi = MappedApiEntity(
            uuid = api.uuid,
            originalApi = OriginalApiEntity(
                baseUrl = api.originalApi.baseUrl
            ),
            name = api.name,
        )
        collection.insertOne(mappedApi)
        return api.uuid.toString()
    }

    private companion object {
        const val MAPPED_API = "MappedApi"
        const val ROUTES = "routes"
    }
}

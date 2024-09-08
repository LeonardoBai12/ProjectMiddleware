package io.lb.impl.mongo.database.service

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.HttpStatusCode
import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.service.DatabaseService
import io.lb.common.shared.error.MiddlewareException
import io.lb.impl.mongo.database.model.MappedApiEntity
import io.lb.impl.mongo.database.model.MappedRouteEntity
import io.lb.impl.mongo.database.model.toEntity
import kotlinx.coroutines.flow.singleOrNull

/**
 * Service implementation for interacting with the database.
 *
 * @constructor Creates a database service implementation with the given database.
 */
internal class DatabaseServiceImpl(
    database: MongoDatabase
) : DatabaseService {
    private val collection = database.getCollection<MappedApiEntity>(MAPPED_API)

    override suspend fun queryAllMappedRoutes(): List<MappedRoute> {
        val routes = mutableListOf<MappedRoute>()

        collection.find<MappedApiEntity>().collect { api ->
            api.routes.forEach { route ->
                routes.add(route.toRoute(api.toMappedApi()))
            }
        }

        return routes
    }

    override suspend fun queryMappedApi(originalBaseUrl: String): MappedApi? {
        val queryParams = Filters.eq(MappedApiEntity::originalBaseUrl.name, originalBaseUrl)
        val api = collection.find<MappedApiEntity>(queryParams)
            .limit(1)
            .singleOrNull()

        api ?: return null

        return MappedApi(
            uuid = api.uuid,
            originalApi = OriginalApi(
                baseUrl = api.originalBaseUrl
            ),
        )
    }

    override suspend fun queryMappedRoutes(originalBaseUrl: String): List<MappedRoute> {
        val queryParams = Filters.eq(MappedApiEntity::originalBaseUrl.name, originalBaseUrl)
        val api = collection.find<MappedApiEntity>(queryParams)
            .limit(1)
            .singleOrNull()

        api ?: throw MiddlewareException(
            code = HttpStatusCode.NotFound.value,
            message = "Couldn't find mapped API."
        )

        val mappedApi = MappedApi(
            uuid = originalBaseUrl,
            originalApi = OriginalApi(
                baseUrl = api.originalBaseUrl
            ),
        )

        return api.routes.map {
            it.toRoute(mappedApi)
        }
    }

    override suspend fun createMappedRoute(route: MappedRoute): String {
        val queryParams = Filters.eq(MappedApiEntity::originalBaseUrl.name, route.mappedApi.originalApi.baseUrl)
        val api = collection.find<MappedApiEntity>(queryParams)
            .limit(1)
            .singleOrNull()

        api ?: throw MiddlewareException(
            code = HttpStatusCode.NotFound.value,
            message = "Couldn't find mapped API."
        )

        val routes = mutableListOf<MappedRouteEntity>().apply {
            addAll(api.routes)
            add(route.toEntity())
        }

        val updateParams = Updates.combine(
            Updates.set(MappedApiEntity::routes.name, routes)
        )
        collection.updateOne(queryParams, updateParams)
        return route.uuid
    }

    override suspend fun updateMappedRoute(route: MappedRoute): String {
        val queryParams = Filters.eq(MappedApiEntity::originalBaseUrl.name, route.mappedApi.originalApi.baseUrl)
        val api = collection.find<MappedApiEntity>(queryParams)
            .limit(1)
            .singleOrNull()
        val routeEntity = api?.routes?.find { it.originalRoute.path == route.originalRoute.path }

        routeEntity ?: throw MiddlewareException(
            code = HttpStatusCode.NotFound.value,
            message = "Couldn't find mapped route."
        )

        val routes = mutableListOf<MappedRouteEntity>().apply {
            addAll(api.routes)
            if (api.routes.any { it.originalRoute.path == route.originalRoute.path }) {
                remove(routeEntity)
            }
            add(route.toEntity())
        }

        val updateParams = Updates.combine(
            Updates.set(MappedApiEntity::routes.name, routes),
        )
        collection.updateOne(queryParams, updateParams)
        return routeEntity.uuid
    }

    override suspend fun createMappedApi(api: MappedApi): String {
        val mappedApi = MappedApiEntity(
            uuid = api.uuid,
            originalBaseUrl = api.originalApi.baseUrl,
        )
        collection.insertOne(mappedApi)
        return api.uuid
    }

    override suspend fun updateMappedApi(api: MappedApi) {
        val queryParams = Filters.eq(MappedApiEntity::uuid.name, api.uuid)
        val apiEntity = collection.find<MappedApiEntity>(queryParams)
            .limit(1)
            .singleOrNull()

        apiEntity ?: throw MiddlewareException(
            code = HttpStatusCode.NotFound.value,
            message = "Couldn't find mapped API."
        )

        val updateParams = Updates.combine(
            Updates.set(MappedApiEntity::originalBaseUrl.name, apiEntity.originalBaseUrl),
            Updates.set(MappedApiEntity::routes.name, apiEntity.routes),
        )
        collection.updateOne(queryParams, updateParams)
    }

    private companion object {
        const val MAPPED_API = "MappedApi"
    }
}

package io.lb.mongo.service

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.HttpStatusCode
import io.lb.core.error.MiddlewareException
import io.lb.data.model.MappedApi
import io.lb.data.model.MappedRoute
import io.lb.data.model.OriginalApi
import io.lb.data.service.DatabaseService
import io.lb.mongo.model.MappedApiEntity
import io.lb.mongo.model.MappedRouteEntity
import io.lb.mongo.model.toEntity
import kotlinx.coroutines.flow.singleOrNull
import java.util.UUID

/**
 * Service implementation for interacting with the database.
 *
 * @constructor Creates a database service implementation with the given database.
 */
class DatabaseServiceImpl(
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

    override suspend fun queryMappedApi(apiUuid: String): MappedApi? {
        val queryParams = Filters.eq(MappedApiEntity::uuid.name, apiUuid)
        val api = collection.find<MappedApiEntity>(queryParams)
            .limit(1)
            .singleOrNull()

        api ?: return null

        return MappedApi(
            uuid = api.uuid,
            originalApi = api.originalApi,
            name = api.name,
        )
    }

    override suspend fun queryMappedRoutes(apiUuid: String): List<MappedRoute> {
        val queryParams = Filters.eq(MappedApiEntity::uuid.name, UUID.fromString(apiUuid))
        val api = collection.find<MappedApiEntity>(queryParams)
            .limit(1)
            .singleOrNull()

        api ?: throw MiddlewareException(
            code = HttpStatusCode.NotFound.value,
            message = "Couldn't find mapped API."
        )

        val mappedApi = MappedApi(
            uuid = UUID.fromString(apiUuid),
            originalApi = api.originalApi,
            name = api.name
        )

        return api.routes.map {
            it.toRoute(mappedApi)
        }
    }

    override suspend fun createMappedRoute(route: MappedRoute) {
        val queryParams = Filters.eq(MappedApiEntity::uuid.name, UUID.fromString(route.mappedApi.uuid.toString()))
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
    }

    override suspend fun updateMappedRoute(route: MappedRoute) {
        val queryParams = Filters.eq(MappedRouteEntity::uuid.name, UUID.fromString(route.uuid.toString()))
        val routeEntity = collection.find<MappedRouteEntity>(queryParams)
            .limit(1)
            .singleOrNull()

        routeEntity ?: throw MiddlewareException(
            code = HttpStatusCode.NotFound.value,
            message = "Couldn't find mapped route."
        )

        val updateParams = Updates.combine(
            Updates.set(MappedRouteEntity::uuid.name, routeEntity.uuid),
            Updates.set(MappedRouteEntity::path.name, routeEntity.path),
            Updates.set(MappedRouteEntity::originalRoute.name, routeEntity.originalRoute),
            Updates.set(MappedRouteEntity::method.name, routeEntity.method),
            Updates.set(MappedRouteEntity::authHeader.name, routeEntity.authHeader),
            Updates.set(MappedRouteEntity::headers.name, routeEntity.headers),
            Updates.set(MappedRouteEntity::query.name, routeEntity.query),
        )
        collection.updateOne(queryParams, updateParams)
    }

    override suspend fun createMappedApi(api: MappedApi): String {
        val mappedApi = MappedApiEntity(
            uuid = api.uuid,
            originalApi = OriginalApi(
                baseUrl = api.originalApi.baseUrl
            ),
            name = api.name,
        )
        collection.insertOne(mappedApi)
        return api.uuid.toString()
    }

    override suspend fun updateMappedApi(api: MappedApi) {
        val queryParams = Filters.eq(MappedApiEntity::uuid.name, UUID.fromString(api.uuid.toString()))
        val apiEntity = collection.find<MappedApiEntity>(queryParams)
            .limit(1)
            .singleOrNull()

        apiEntity ?: throw MiddlewareException(
            code = HttpStatusCode.NotFound.value,
            message = "Couldn't find mapped API."
        )

        val updateParams = Updates.combine(
            Updates.set(MappedApiEntity::uuid.name, apiEntity.uuid),
            Updates.set(MappedApiEntity::originalApi.name, apiEntity.originalApi),
            Updates.set(MappedApiEntity::name.name, apiEntity.name),
            Updates.set(MappedApiEntity::routes.name, apiEntity.routes),
        )
        collection.updateOne(queryParams, updateParams)
    }

    private companion object {
        const val MAPPED_API = "MappedApi"
    }
}

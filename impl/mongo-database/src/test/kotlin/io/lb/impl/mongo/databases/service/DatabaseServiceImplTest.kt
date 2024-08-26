package io.lb.impl.mongo.databases.service

import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.common.data.service.DatabaseService
import io.lb.common.shared.error.MiddlewareException
import io.lb.impl.mongo.database.model.MappedApiEntity
import io.lb.impl.mongo.database.model.MappedRouteEntity
import io.lb.impl.mongo.database.service.DatabaseServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.bson.conversions.Bson
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class DatabaseServiceImplTest {
    private lateinit var db: MongoDatabase
    private lateinit var collection: MongoCollection<MappedApiEntity>
    private lateinit var service: DatabaseService

    @BeforeEach
    fun setUp() {
        db = mockk(relaxed = true)
        collection = db.getCollection("MappedApi")
        service = DatabaseServiceImpl(db)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When APIs exist, expect return all routes`() = runTest {
        val apis =
            listOf(
                MappedApiEntity(
                    UUID.randomUUID(),
                    OriginalApi("https://teste.com"),
                    "name",
                    listOf(
                        mappedRouteEntity(),
                        mappedRouteEntity(),
                    )
                ),
                MappedApiEntity(
                    UUID.randomUUID(),
                    OriginalApi("https://teste.com"),
                    "name",
                    listOf(
                        mappedRouteEntity(),
                        mappedRouteEntity(),
                        mappedRouteEntity(),
                        mappedRouteEntity(),
                    )
                )
            )

        mockFindApi(apis)

        val result = service.queryAllMappedRoutes()
        advanceUntilIdle()

        assertEquals(6, result.size)
    }

    @Test
    fun `When API exists, expect return it`() = runTest {
        val apiUuid = UUID.randomUUID()
        val api = MappedApiEntity(
            uuid = apiUuid,
            originalApi = OriginalApi("https://test.com"),
            name = "Test API"
        )

        mockkFindApiLimit1(api)

        val result = service.queryMappedApi(apiUuid.toString())
        advanceUntilIdle()

        assertNotNull(result)
        assertEquals(apiUuid.toString(), result?.uuid.toString())
        assertEquals("Test API", result?.name)
    }

    @Test
    fun `When API is not found, expect return null`() = runTest {
        val apiUuid = UUID.randomUUID()

        mockkFindApiLimit1(null)

        val result = service.queryMappedApi(apiUuid.toString())
        advanceUntilIdle()

        assertNull(result)
    }

    @Test
    fun `When API exists, expect return all routes`() = runTest {
        val apiUuid = UUID.randomUUID()
        val mappedRoutes = listOf(
            mappedRouteEntity(),
            mappedRouteEntity(),
            mappedRouteEntity(),
            mappedRouteEntity(),
            mappedRouteEntity(),
        )
        val api = MappedApiEntity(
            uuid = apiUuid,
            originalApi = OriginalApi("https://test.com"),
            name = "Test API",
            routes = mappedRoutes
        )

        mockkFindApiLimit1(api)

        val result = service.queryMappedRoutes(apiUuid.toString())
        advanceUntilIdle()

        assertEquals(5, result.size)
    }

    @Test
    fun `When API is not found, expect throw MiddlewareException`() = runTest {
        val apiUuid = UUID.randomUUID()

        mockkFindApiLimit1(null)

        val exception = assertThrows<MiddlewareException> {
            service.queryMappedRoutes(apiUuid.toString())
            advanceUntilIdle()
        }

        assertEquals(404, exception.code)
        assertEquals("Couldn't find mapped API.", exception.message)
    }

    @Test
    fun `When API exists, expect create route`() = runTest {
        val apiUuid = UUID.randomUUID()
        val existingRoutes = listOf(
            mappedRouteEntity(),
            mappedRouteEntity()
        )
        val newRoute = mappedRoute().copy(mappedApi = MappedApi(apiUuid, OriginalApi("https://test.com"), "Test API"))
        val api = MappedApiEntity(
            uuid = apiUuid,
            originalApi = OriginalApi("https://test.com"),
            name = "Test API",
            routes = existingRoutes
        )

        mockkFindApiLimit1(api)
        coEvery { collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>()) } returns mockk()

        service.createMappedRoute(newRoute)
        advanceUntilIdle()

        coVerify { collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>()) }
    }

    @Test
    fun `When API is not found on route creation, expect throw MiddlewareException`() = runTest {
        val newRoute = mappedRoute().copy(
            mappedApi = MappedApi(UUID.randomUUID(), OriginalApi("https://test.com"), "Test API")
        )

        mockFindApi(emptyList())

        val exception = assertThrows<MiddlewareException> {
            service.createMappedRoute(newRoute)
            advanceUntilIdle()
        }

        assertEquals(404, exception.code)
        assertEquals("Couldn't find mapped API.", exception.message)
        coVerify(exactly = 0) { collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>()) }
    }

    @Test
    fun `When route is found, expect update it`() = runTest {
        val routeUuid = UUID.randomUUID()
        val existingRoute = mappedRouteEntity().copy(uuid = routeUuid)

        mockkFindRouteLimit1(existingRoute)
        coEvery { collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>()) } returns mockk()

        service.updateMappedRoute(
            existingRoute.toRoute(MappedApi(UUID.randomUUID(), OriginalApi("https://test.com"), "Test API"))
        )
        advanceUntilIdle()

        coVerify {
            collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>())
        }
    }

    @Test
    fun `When route is not found, expect throw MiddlewareException`() = runTest {
        val routeUuid = UUID.randomUUID()
        val updatedRoute = mappedRoute().copy(uuid = routeUuid)

        mockkFindRouteLimit1(null)
        coEvery { collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>()) } returns mockk()

        val exception = assertThrows<MiddlewareException> {
            service.updateMappedRoute(updatedRoute)
            advanceUntilIdle()
        }

        assertEquals(404, exception.code)
        assertEquals("Couldn't find mapped route.", exception.message)
        coVerify(exactly = 0) {
            collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>())
        }
    }

    @Test
    fun `When API does not exist, expect create it`() = runTest {
        val apiUuid = UUID.randomUUID()
        val newApi = MappedApi(apiUuid, OriginalApi("https://test.com"), "Test API")

        coEvery { collection.insertOne(any<MappedApiEntity>(), any<InsertOneOptions>()) } returns mockk()

        val result = service.createMappedApi(newApi)
        advanceUntilIdle()

        assertEquals(apiUuid.toString(), result)
        coVerify {
            collection.insertOne(
                MappedApiEntity(
                    uuid = apiUuid,
                    originalApi = newApi.originalApi,
                    name = newApi.name
                ),
                any<InsertOneOptions>()
            )
        }
    }

    @Test
    fun `When it exists, expect update it`() = runTest {
        val apiUuid = UUID.randomUUID()
        val existingApi = MappedApiEntity(
            apiUuid,
            OriginalApi("https://test.com"),
            "Test API"
        )
        val updatedApi = MappedApi(apiUuid, OriginalApi("https://test.com"), "Updated API")

        mockFindApi(listOf(existingApi))
        mockkFindApiLimit1(existingApi)
        coEvery { collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>()) } returns mockk()

        service.updateMappedApi(updatedApi)
        advanceUntilIdle()

        coVerify {
            collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>())
        }
    }

    @Test
    fun `When API is not found expect throw MiddlewareException`() = runTest {
        val apiUuid = UUID.randomUUID()
        val updatedApi = MappedApi(apiUuid, OriginalApi("https://test.com"), "Updated API")

        mockFindApi(emptyList())

        val exception = assertThrows<MiddlewareException> {
            service.updateMappedApi(updatedApi)
            advanceUntilIdle()
        }

        assertEquals(404, exception.code)
        assertEquals("Couldn't find mapped API.", exception.message)
        coVerify(exactly = 0) {
            collection.updateOne(any<Bson>(), any<Bson>(), any<UpdateOptions>())
        }
    }

    private fun mockkFindRouteLimit1(route: MappedRouteEntity?) {
        val flow = mockk<FindFlow<MappedRouteEntity>>(relaxed = true)

        coEvery {
            collection.find<MappedRouteEntity>(any<Bson>()).limit(1)
        } coAnswers {
            flow
        }

        coEvery { flow.collect(any()) } coAnswers {
            val collector = it.invocation.args[0] as FlowCollector<MappedRouteEntity?>
            collector.emit(route)
        }
    }

    private fun mockkFindApiLimit1(api: MappedApiEntity?) {
        val flow = mockk<FindFlow<MappedApiEntity>>(relaxed = true)

        coEvery {
            collection.find<MappedApiEntity>(any<Bson>()).limit(1)
        } coAnswers {
            flow
        }

        coEvery { flow.collect(any()) } coAnswers {
            val collector = it.invocation.args[0] as FlowCollector<MappedApiEntity?>
            collector.emit(api)
        }
    }

    private fun mockFindApi(apis: List<MappedApiEntity>) {
        coEvery { collection.find<MappedApiEntity>().collect(any()) } coAnswers {
            val collector = it.invocation.args[0] as FlowCollector<MappedApiEntity>
            apis.forEach { api ->
                collector.emit(api)
            }
        }
    }

    private fun mappedRouteEntity() = MappedRouteEntity(
        uuid = UUID.randomUUID(),
        path = "/path",
        originalRoute = OriginalRoute(
            path = "/path",
            method = MiddlewareHttpMethods.Get,
            body = "",
            originalApi = OriginalApi("https://teste.com")
        ),
        method = MiddlewareHttpMethods.Get,
        preConfiguredQueries = mapOf("query" to "value"),
        preConfiguredHeaders = mapOf("header" to "value"),
        preConfiguredBody = "",
        rulesAsString = null,
    )

    private fun mappedRoute() = mappedRouteEntity()
        .toRoute(
            MappedApi(
                UUID.randomUUID(),
                OriginalApi("https://teste.com"),
                "name"
            )
        )
}

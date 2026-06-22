package io.lb.middleware.mapper.service

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.OriginalResponse
import io.lb.common.shared.error.MiddlewareException
import io.lb.middleware.mapper.model.NewBodyField
import io.lb.middleware.mapper.model.NewBodyMappingRule
import io.lb.middleware.mapper.model.OldBodyField
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MapperServiceImplTest {

    private lateinit var mapperService: MapperServiceImpl

    @BeforeEach
    fun setUp() {
        mapperService = MapperServiceImpl()
    }

    @Test
    fun `mapResponse should return a MappedResponse with correct statusCode and mapped body`() {
        val originalResponse = createOriginalResponse()
        val mappedResponse: MappedResponse = mapperService.mapResponse(getMappingRule(), originalResponse)

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(expectedMappedResponse(), json.parseToJsonElement(mappedResponse.body!!).toString())
    }

    @Test
    fun `mapResponse should return a MappedResponse with correct statusCode and mapped body with empty values`() {
        val originalResponse = createOriginalResponse()
        val mappedResponse: MappedResponse = mapperService.mapResponse(getMappingRule(false), originalResponse)

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(expectedMappedResponseWithEmptyValues(), json.parseToJsonElement(mappedResponse.body!!).toString())
    }

    @Test
    fun `responseJsonPreview should return a JSON string with the mapped response`() {
        val originalResponse = createOriginalResponse()
        val jsonPreview: String = mapperService.responseJsonPreview(getMappingRule(), originalResponse.body!!)

        assertEquals(expectedMappedResponse(), json.parseToJsonElement(jsonPreview).toString())
    }

    @Test
    fun `mapResponse should throw MiddlewareException when mapping rules are invalid`() {
        val originalResponse = createOriginalResponse()

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse("Invalid json", originalResponse)
        }

        assertEquals(
            "Failed to parse mapping rules. Checkout our documentation: " +
                "https://github.com/LeonardoBai12-Org/ProjectMiddleware",
            exception.message
        )
    }

    @Test
    fun `mapResponse should throw MiddlewareException when oldBodyField is missing`() {
        val invalidMappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf("nonExistentField" to NewBodyField("nonExistentField", "String")),
                oldBodyFields = mapOf("idMeal" to OldBodyField(listOf("idMeal"), "Int", parents = listOf("meals")))
            )
        )
        val originalResponse = createOriginalResponse()

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse(invalidMappingRule, originalResponse)
        }

        assertEquals("Mapping rule for new key 'nonExistentField' is missing in old body fields.", exception.message)
    }

    @Test
    fun `mapResponse should throw MiddlewareException when parent key is not found in original JSON`() {
        val invalidMappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf("idMeal" to NewBodyField("id", "Int")),
                oldBodyFields = mapOf("idMeal" to OldBodyField(listOf("idMeal"), "Int", parents = listOf("parent")))
            )
        )
        val originalResponse = OriginalResponse(
            statusCode = 200,
            body = """
                {
                   "meals":[
                      {
                         "idMeal":"53058",
                         "strMeal":"Croatian Bean Stew",
                         "strDrinkAlternate":null,
                         "strCategory":"Beef",
                         "strArea":"Croatian"
                      }
                   ]
                }
            """.trimIndent()
        )

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse(invalidMappingRule, originalResponse)
        }

        assertEquals("Parent key 'parent' not found in original JSON.", exception.message)
    }

    @Test
    fun `mapResponse should throw MiddlewareException when all extracted values are empty`() {
        val mappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf("idMeal" to NewBodyField("id", "Int")),
                oldBodyFields = mapOf("idMeal" to OldBodyField(listOf("idMeal"), "Int", parents = listOf("meals")))
            )
        )
        val emptyValuesOriginalResponse = OriginalResponse(
            statusCode = 200,
            body = """
                {
                   "meals":[
                      {
                         "idMeal":"   ",
                         "strMeal":""
                      }
                   ]
                }
            """.trimIndent()
        )

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse(mappingRule, emptyValuesOriginalResponse)
        }

        assertEquals("All extracted values are empty.", exception.message)
    }

    @Test
    fun `mapResponse should throw MiddlewareException when key is not found in original JSON root`() {
        val mappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf("idMeal" to NewBodyField("id", "Int")),
                oldBodyFields = mapOf("idMeal" to OldBodyField(listOf("idMeal"), "Int"))
            )
        )
        val originalResponse = createOriginalResponse()

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse(mappingRule, originalResponse)
        }

        assertEquals("Key 'idMeal' not found in original JSON root.", exception.message)
    }

    @Test
    fun `mapResponse should return a MappedResponse with concatenated fields`() {
        val originalResponse = createOriginalResponse()
        val mappedResponse: MappedResponse = mapperService.mapResponse(getConcatenatedMappingRule(), originalResponse)

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(expectedMeasuredResponse(), json.parseToJsonElement(mappedResponse.body!!).toString())
    }

    @Test
    fun `mapResponse should return concatenated fields with empty values when ignoreEmptyValues is false`() {
        val originalResponse = createOriginalResponse()
        val mappedResponse: MappedResponse = mapperService.mapResponse(
            getConcatenatedMappingRule(false),
            originalResponse
        )

        assertEquals(200, mappedResponse.statusCode)
        val expected = expectedMeasuredResponseWithEmptyValues()
        assertEquals(expected, json.parseToJsonElement(mappedResponse.body!!).toString())
    }

    @Test
    fun `mapResponse should map Double type field correctly`() {
        val mappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf("rating" to NewBodyField("rating", "Double")),
                oldBodyFields = mapOf("rating" to OldBodyField(listOf("rating"), "Double"))
            )
        )
        val mappedResponse = mapperService.mapResponse(mappingRule, createFlatOriginalResponse())

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(
            json.parseToJsonElement("""{"rating":4.5}""").toString(),
            json.parseToJsonElement(mappedResponse.body!!).toString()
        )
    }

    @Test
    fun `mapResponse should map Boolean type field correctly`() {
        val mappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf("available" to NewBodyField("available", "Boolean")),
                oldBodyFields = mapOf("available" to OldBodyField(listOf("available"), "Boolean"))
            )
        )
        val mappedResponse = mapperService.mapResponse(mappingRule, createFlatOriginalResponse())

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(
            json.parseToJsonElement("""{"available":true}""").toString(),
            json.parseToJsonElement(mappedResponse.body!!).toString()
        )
    }

    @Test
    fun `mapResponse should map root-level fields without parents`() {
        val mappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf(
                    "id" to NewBodyField("id", "Int"),
                    "title" to NewBodyField("title", "String")
                ),
                oldBodyFields = mapOf(
                    "id" to OldBodyField(listOf("id"), "Int"),
                    "title" to OldBodyField(listOf("title"), "String")
                )
            )
        )
        val mappedResponse = mapperService.mapResponse(mappingRule, createFlatOriginalResponse())

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(
            json.parseToJsonElement("""{"id":101,"title":"Pasta Carbonara"}""").toString(),
            json.parseToJsonElement(mappedResponse.body!!).toString()
        )
    }

    @Test
    fun `mapResponse should preserve non-200 status code`() {
        val mappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf("id" to NewBodyField("id", "Int")),
                oldBodyFields = mapOf("id" to OldBodyField(listOf("id"), "Int"))
            )
        )
        val originalResponse = OriginalResponse(statusCode = 404, body = """{"id":"42"}""")
        val mappedResponse = mapperService.mapResponse(mappingRule, originalResponse)

        assertEquals(404, mappedResponse.statusCode)
        assertEquals(
            json.parseToJsonElement("""{"id":42}""").toString(),
            json.parseToJsonElement(mappedResponse.body!!).toString()
        )
    }

    @Test
    fun `mapResponse should map fields with multiple parent levels`() {
        val mappingRule = Json.encodeToString(
            NewBodyMappingRule(
                newBodyFields = mapOf(
                    "id" to NewBodyField("id", "Int"),
                    "name" to NewBodyField("name", "String")
                ),
                oldBodyFields = mapOf(
                    "id" to OldBodyField(listOf("id"), "Int", parents = listOf("data", "recipes")),
                    "name" to OldBodyField(listOf("name"), "String", parents = listOf("data", "recipes"))
                )
            )
        )
        val mappedResponse = mapperService.mapResponse(mappingRule, createDeepNestedOriginalResponse())

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(
            json.parseToJsonElement("""{"id":200,"name":"Tiramisu"}""").toString(),
            json.parseToJsonElement(mappedResponse.body!!).toString()
        )
    }

    @Test
    fun `mapResponse should omit null String fields when ignoreEmptyValues is true`() {
        val mappingRule = Json.encodeToString(
            NewBodyMappingRule(
                ignoreEmptyValues = true,
                newBodyFields = mapOf(
                    "idMeal" to NewBodyField("id", "Int"),
                    "drinkAlternate" to NewBodyField("comment", "String")
                ),
                oldBodyFields = mapOf(
                    "idMeal" to OldBodyField(listOf("idMeal"), "Int", parents = listOf("meals")),
                    "drinkAlternate" to OldBodyField(listOf("strDrinkAlternate"), "String", parents = listOf("meals"))
                )
            )
        )
        val mappedResponse = mapperService.mapResponse(mappingRule, createOriginalResponse())
        val body = json.parseToJsonElement(mappedResponse.body!!).jsonObject

        assertFalse(body.containsKey("comment"))
    }

    @Test
    fun `responseJsonPreview should throw MiddlewareException when mapping rules are invalid`() {
        val exception = assertThrows<MiddlewareException> {
            mapperService.responseJsonPreview("Invalid json", createOriginalResponse().body!!)
        }

        assertEquals(
            "Failed to parse mapping rules. Checkout our documentation: " +
                "https://github.com/LeonardoBai12-Org/ProjectMiddleware",
            exception.message
        )
    }
}

val json = Json {
    prettyPrint = true
}

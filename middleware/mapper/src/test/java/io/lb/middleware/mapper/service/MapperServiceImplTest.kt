package io.lb.middleware.mapper.service

import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.OriginalResponse
import io.lb.common.shared.error.MiddlewareException
import io.lb.middleware.mapper.model.NewBodyField
import io.lb.middleware.mapper.model.NewBodyMappingRule
import io.lb.middleware.mapper.model.OldBodyField
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
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
        assertEquals(expectedMappedResponse(), mappedResponse.body)
    }

    @Test
    fun `mapResponse should return a MappedResponse with correct statusCode and mapped body with empty values`() {
        val originalResponse = createOriginalResponse()
        val mappedResponse: MappedResponse = mapperService.mapResponse(getMappingRule(false), originalResponse)

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(expectedMappedResponseWithEmptyValues(), mappedResponse.body)
    }

    @Test
    fun `responseJsonPreview should return a JSON string with the mapped response`() {
        val originalResponse = createOriginalResponse()
        val jsonPreview: String = mapperService.responseJsonPreview(getMappingRule(), originalResponse)

        assertEquals(expectedMappedResponse(), jsonPreview)
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
    fun `mapResponse shaould return a MappedResponse with correct statusCode and mapped body`() {
        val originalResponse = createOriginalResponse()
        val mappedResponse: MappedResponse = mapperService.mapResponse(getConcatenatedMappingRule(), originalResponse)

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(expectedMeasuredResponse(), mappedResponse.body)
    }
}

val json = Json {
    prettyPrint = true
}

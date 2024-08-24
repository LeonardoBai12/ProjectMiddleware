package io.lb.middleware.mapper.service

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedResponse
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareHttpMethods
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
        val route = createMappedRoute()
        val originalResponse = createOriginalResponse()

        val mappedResponse: MappedResponse = mapperService.mapResponse(route, originalResponse)

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(expectedMappedResponse(), mappedResponse.body)
    }

    @Test
    fun `mapResponse should return a MappedResponse with correct statusCode and mapped body with empty values`() {
        val route = createMappedRoute(false)
        val originalResponse = createOriginalResponse()

        val mappedResponse: MappedResponse = mapperService.mapResponse(route, originalResponse)

        assertEquals(200, mappedResponse.statusCode)
        assertEquals(expectedMappedResponseWithEmptyValues(), mappedResponse.body)
    }

    @Test
    fun `responseJsonPreview should return a JSON string with the mapped response`() {
        val route = createMappedRoute()
        val originalResponse = createOriginalResponse()

        val jsonPreview: String = mapperService.responseJsonPreview(route, originalResponse)

        assertEquals(expectedMappedResponse(), jsonPreview)
    }

    @Test
    fun `mapResponse should throw MiddlewareException when mapping rules are invalid`() {
        val route = createMappedRoute(rulesAsString = "invalid json")
        val originalResponse = createOriginalResponse()

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse(route, originalResponse)
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
        val route = createMappedRoute(rulesAsString = invalidMappingRule)
        val originalResponse = createOriginalResponse()

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse(route, originalResponse)
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
        val route = createMappedRoute(rulesAsString = invalidMappingRule)
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
            mapperService.mapResponse(route, originalResponse)
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
        val route = createMappedRoute(rulesAsString = mappingRule)
        val emptyValuesOriginalResponse = OriginalResponse(
            statusCode = 200,
            body = """
                {
                   "meals":[
                      {
                         "idMeal":"null"
                      }
                   ]
                }
            """.trimIndent()
        )

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse(route, emptyValuesOriginalResponse)
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
        val route = createMappedRoute(rulesAsString = mappingRule)
        val originalResponse = createOriginalResponse()

        val exception = assertThrows<MiddlewareException> {
            mapperService.mapResponse(route, originalResponse)
        }

        assertEquals("Key 'idMeal' not found in original JSON root.", exception.message)
    }

    private fun createMappedRoute(
        ignoreEmptyValues: Boolean = true,
        rulesAsString: String = getMappingRule(ignoreEmptyValues)
    ): MappedRoute {
        return MappedRoute(
            path = "getTeryiaki",
            mappedApi = MappedApi(
                name = "Test API",
                originalApi = OriginalApi("https://www.themealdb.com/api/")
            ),
            method = MiddlewareHttpMethods.Get,
            originalRoute = OriginalRoute(
                path = "json/v1/1/lookup.php?i=52772",
                method = MiddlewareHttpMethods.Get,
                originalApi = OriginalApi("https://www.themealdb.com/api/"),
                body = null
            ),
            rulesAsString = rulesAsString
        )
    }

    private fun createOriginalResponse(): OriginalResponse {
        return OriginalResponse(
            statusCode = 200,
            body = """
            {
               "meals":[
                  {
                     "idMeal":"53058",
                     "strMeal":"Croatian Bean Stew",
                     "strDrinkAlternate":null,
                     "strCategory":"Beef",
                     "strArea":"Croatian",
                     "strInstructions":"Heat the oil in a pan. Add the chopped vegetables and saut\u00e9 until tender. Take a pot, empty the beans together with the vegetables into it, put the sausages inside and cook for further 20 minutes on a low heat. Or, put it in an oven and bake it at a temperature of 180\u00baC\/350\u00baF for 30 minutes. This dish is even better reheated the next day.",
                     "strMealThumb":"https:\/\/www.themealdb.com\/images\/media\/meals\/tnwy8m1628770384.jpg",
                     "strTags":"Warming, Soup, Beans",
                     "strYoutube":"https:\/\/www.youtube.com\/watch?v=mrjnQal3S1A",
                     $oldIngredients
                     "strSource":"https:\/\/www.visit-croatia.co.uk\/croatian-cuisine\/croatian-recipes\/",
                     "strImageSource":null,
                     "strCreativeCommonsConfirmed":null,
                     "dateModified":null
                  }
               ]
            }
            """.trimIndent()
        )
    }

    private val oldIngredients = """
        "strIngredient1":"Cannellini Beans",
        "strIngredient2":"Vegetable Oil",
        "strIngredient3":"Tomatoes",
        "strIngredient4":"Challots",
        "strIngredient5":"Garlic",
        "strIngredient6":"Parsley",
        "strIngredient7":"Chorizo",
        "strIngredient8":"",
        "strIngredient9":"",
        "strIngredient10":"",
        "strIngredient11":"",
        "strIngredient12":"",
        "strIngredient13":"",
        "strIngredient14":"",
        "strIngredient15":"",
        "strIngredient16":"",
        "strIngredient17":"",
        "strIngredient18":"",
        "strIngredient19":"",
        "strIngredient20":"",
        "strMeasure1":"2 cans",
        "strMeasure2":"3 tbs",
        "strMeasure3":"2 cups ",
        "strMeasure4":"5",
        "strMeasure5":"2 cloves",
        "strMeasure6":"Pinch",
        "strMeasure7":"1\/2 kg chopped ",
        "strMeasure8":" ",
        "strMeasure9":" ",
        "strMeasure10":" ",
        "strMeasure11":" ",
        "strMeasure12":" ",
        "strMeasure13":" ",
        "strMeasure14":" ",
        "strMeasure15":" ",
        "strMeasure16":" ",
        "strMeasure17":" ",
        "strMeasure18":" ",
        "strMeasure19":" ",
        "strMeasure20":" ",
    """

    private fun expectedMappedResponse() =
        json.parseToJsonElement(
            """
        {
          "id":53058,
          "name":"Croatian Bean Stew",
          "thumbnail":"https://www.themealdb.com/images/media/meals/tnwy8m1628770384.jpg",
          "ingredients":[
            "Cannellini Beans",
            "Vegetable Oil",
            "Tomatoes",
            "Challots",
            "Garlic",
            "Parsley",
            "Chorizo"
          ],
          "measures":[
            "2 cans",
            "3 tbs",
            "2 cups ",
            "5",
            "2 cloves",
            "Pinch",
            "1/2 kg chopped "
          ]
        }
            """.trimIndent()
        ).toString()

    private fun getMappingRule(ignoreEmptyValues: Boolean = true) =
        json.parseToJsonElement(
            """
        {
          "ignoreEmptyValues":$ignoreEmptyValues,
          $newFields
          $oldFields
        }
            """.trimIndent()
        ).toString()

    private val newFields = """        
        "newBodyFields":{
          "idMeal":{
            "key":"id",
            "type":"Int"
          },
          "meal":{
            "key":"name",
            "type":"String"
          },
          "mealThumb":{
            "key":"thumbnail",
            "type":"String"
          },
          "ingredients":{
            "key":"ingredients",
            "type":"String"
          },
          "measures":{
            "key":"measures",
            "type":"String"
          }
        },
        """

    private val parents = """
        "parents":[
          "meals"
        ]
    """

    private val measures = """                
        "strMeasure1",
        "strMeasure2",
        "strMeasure3",
        "strMeasure4",
        "strMeasure5",
        "strMeasure6",
        "strMeasure7",
        "strMeasure8",
        "strMeasure9",
        "strMeasure10",
        "strMeasure11",
        "strMeasure12",
        "strMeasure13",
        "strMeasure14",
        "strMeasure15",
        "strMeasure16",
        "strMeasure17",
        "strMeasure18",
        "strMeasure19",
        "strMeasure20"
    """

    private val ingredients = """
         "strIngredient1",
         "strIngredient2",
         "strIngredient3",
         "strIngredient4",
         "strIngredient5",
         "strIngredient6",
         "strIngredient7",
         "strIngredient8",
         "strIngredient9",
         "strIngredient10",
         "strIngredient11",
         "strIngredient12",
         "strIngredient13",
         "strIngredient14",
         "strIngredient15",
         "strIngredient16",
         "strIngredient17",
         "strIngredient18",
         "strIngredient19",
         "strIngredient20"
    """

    private fun expectedMappedResponseWithEmptyValues() =
        json.parseToJsonElement(
            """
        {
          "id":53058,
          "name":"Croatian Bean Stew",
          "thumbnail":"https://www.themealdb.com/images/media/meals/tnwy8m1628770384.jpg",
          "ingredients":[
            "Cannellini Beans",
            "Vegetable Oil",
            "Tomatoes",
            "Challots",
            "Garlic",
            "Parsley",
            "Chorizo",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
          ],
          "measures":[
            "2 cans",
            "3 tbs",
            "2 cups ",
            "5",
            "2 cloves",
            "Pinch",
            "1/2 kg chopped ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " ",
            " "
          ]
        }
            """.trimIndent()
        ).toString()

    private val oldFields = """
        "oldBodyFields":{
          "idMeal":{
            "keys":[
              "idMeal"
            ],
            "type":"Int",
            $parents
          },
          "meal":{
            "keys":[
              "strMeal"
            ],
            "type":"String",
            $parents
          },
          "mealThumb":{
            "keys":[
              "strMealThumb"
            ],
            "type":"String",
            $parents
          },
          "tags":{
            "keys":[
              "strTags"
            ],
            "type":"String"
          },
          "youtube":{
            "keys":[
              "strYoutube"
            ],
            "type":"String"
          },
          "source":{
            "keys":[
              "strSource"
            ],
            "type":"String"
          },
          "dateModified":{
            "keys":[
              "dateModified"
            ],
            "type":"String?"
          },
          "ingredients":{
            "keys":[
              $ingredients
            ],
            "type":"String",
            "parents":[
              "meals"
            ]
          },
          "measures":{
            "keys":[
              $measures
            ],
            "type":"String",
            $parents
          }
        }"""

    private val json = Json {
        prettyPrint = true
    }
}

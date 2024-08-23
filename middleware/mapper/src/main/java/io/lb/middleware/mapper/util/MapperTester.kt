package io.lb.middleware.mapper.util

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareHttpMethods
import io.lb.middleware.mapper.model.NewBodyField
import io.lb.middleware.mapper.model.NewBodyMappingRule
import io.lb.middleware.mapper.model.OldBodyField
import io.lb.middleware.mapper.service.MapperServiceImpl
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Main function to test the mapper service.
 */
internal fun main() {
    val mappingRule = Json.encodeToString(
        getMappingRule().copy(
            ignoreEmptyValues = true
        )
    )
    val mapper = MapperServiceImpl()

    val preview = mapper.responseJsonPreview(
        route = MappedRoute(
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
            rulesAsString = mappingRule
        ),
        originalResponse = OriginalResponse(
            statusCode = 200,
            body = mealResponse,
        )
    )

    println(preview)
}

private val mealResponse = """
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
             "strSource":"https:\/\/www.visit-croatia.co.uk\/croatian-cuisine\/croatian-recipes\/",
             "strImageSource":null,
             "strCreativeCommonsConfirmed":null,
             "dateModified":null
          }
       ]
    }
""".trimIndent()

private fun getMappingRule(): NewBodyMappingRule =
    NewBodyMappingRule(
        newBodyFields = mapOf(
            "idMeal" to NewBodyField("id", "Int"),
            "meal" to NewBodyField("name", "String"),
            "mealThumb" to NewBodyField("thumbnail", "String"),
            "ingredients" to NewBodyField("ingredients", "String"),
            "measures" to NewBodyField("measures", "String")
        ),
        oldBodyFields = mapOf(
            "idMeal" to OldBodyField(listOf("idMeal"), "Int", parents = listOf("meals")),
            "meal" to OldBodyField(listOf("strMeal"), "String", parents = listOf("meals")),
            "drinkAlternate" to OldBodyField(listOf("strDrinkAlternate"), "String?"),
            "category" to OldBodyField(listOf("strCategory"), "String"),
            "area" to OldBodyField(listOf("strArea"), "String"),
            "instructions" to OldBodyField(listOf("strInstructions"), "String"),
            "mealThumb" to OldBodyField(listOf("strMealThumb"), "String"),
            "tags" to OldBodyField(listOf("strTags"), "String"),
            "youtube" to OldBodyField(listOf("strYoutube"), "String"),
            "source" to OldBodyField(listOf("strSource"), "String"),
            "imageSource" to OldBodyField(listOf("strImageSource"), "String?"),
            "creativeCommonsConfirmed" to OldBodyField(
                listOf("strCreativeCommonsConfirmed"),
                "String?"
            ),
            "dateModified" to OldBodyField(listOf("dateModified"), "String?"),
            "ingredients" to OldBodyField(
                keys = ingredientsOriginalFields(),
                type = "String",
                parents = listOf("meals")
            ),
            "measures" to OldBodyField(
                keys = measuresOriginalFields(),
                type = "String",
                parents = listOf("meals")
            )
        )
    )

private fun measuresOriginalFields() = listOf(
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
)

private fun ingredientsOriginalFields() = listOf(
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
)

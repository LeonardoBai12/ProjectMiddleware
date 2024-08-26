package io.lb.middleware.mapper.service

import io.lb.common.data.model.MappedApi
import io.lb.common.data.model.MappedRoute
import io.lb.common.data.model.OriginalApi
import io.lb.common.data.model.OriginalResponse
import io.lb.common.data.model.OriginalRoute
import io.lb.common.data.request.MiddlewareHttpMethods

internal fun createMappedRoute(
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

internal fun createOriginalResponse(): OriginalResponse {
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
                     $OLD_INGREDIENTS
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

internal const val OLD_INGREDIENTS = """
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

internal fun expectedMappedResponseWithEmptyValues() =
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
            "2 cups",
            "5",
            "2 cloves",
            "Pinch",
            "1/2 kg chopped",
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
          ]
        }
        """.trimIndent()
    ).toString()

internal const val NEW_FIELDS = """        
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

internal const val PARENTS = """
        "parents":[
          "meals"
        ]
    """

internal const val MEASURES = """                
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

internal const val INGREDIENTS = """
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

internal val oldFields = """
        "oldBodyFields":{
          "idMeal":{
            "keys":[
              "idMeal"
            ],
            "type":"Int",
            $PARENTS
          },
          "meal":{
            "keys":[
              "strMeal"
            ],
            "type":"String",
            $PARENTS
          },
          "mealThumb":{
            "keys":[
              "strMealThumb"
            ],
            "type":"String",
            $PARENTS
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
              $INGREDIENTS
            ],
            "type":"String",
            "parents":[
              "meals"
            ]
          },
          "measures":{
            "keys":[
              $MEASURES
            ],
            "type":"String",
            $PARENTS
          }
        }"""

internal fun getMappingRule(ignoreEmptyValues: Boolean = true) =
    json.parseToJsonElement(
        """
        {
          "ignoreEmptyValues":$ignoreEmptyValues,
          $NEW_FIELDS
          $oldFields
        }
        """.trimIndent()
    ).toString()

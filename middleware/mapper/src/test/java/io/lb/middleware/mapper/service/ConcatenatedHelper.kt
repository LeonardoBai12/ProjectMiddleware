package io.lb.middleware.mapper.service

internal fun getConcatenatedMappingRule() =
    json.parseToJsonElement(
        """
        {
          "ignoreEmptyValues":true,
          $concatenatedNewFields
          $concatenatedOldFields
        }
        """.trimIndent()
    ).toString()

internal val measuresWithIngredients = """
        "strMeasure1, strIngredient1",
        "strMeasure2, strIngredient2",
        "strMeasure3, strIngredient3",
        "strMeasure4, strIngredient4",
        "strMeasure5, strIngredient5",
        "strMeasure6, strIngredient6",
        "strMeasure7, strIngredient7",
        "strMeasure8, strIngredient8",
        "strMeasure9, strIngredient9",
        "strMeasure10, strIngredient10",
        "strMeasure11, strIngredient11",
        "strMeasure12, strIngredient12",
        "strMeasure13, strIngredient13",
        "strMeasure14, strIngredient14",
        "strMeasure15, strIngredient15",
        "strMeasure16, strIngredient16",
        "strMeasure17, strIngredient17",
        "strMeasure18, strIngredient18",
        "strMeasure19, strIngredient19",
        "strMeasure20, strIngredient20"
""".trimIndent()

internal val concatenatedOldFields = """
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
          "measuredIngredients":{
            "keys":[
              $measuresWithIngredients
            ],
            "type":"String",
            "parents":[
              "meals"
            ]
          }
        }"""

private const val concatenatedNewFields = """        
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
          "measuredIngredients":{
            "key":"ingredients",
            "type":"String"
          }
        },
        """

internal fun expectedMeasuredResponse() =
    json.parseToJsonElement(
        """
        {
          "id":53058,
          "name":"Croatian Bean Stew",
          "ingredients":[
            "2 cans Cannellini Beans",
            "3 tbs Vegetable Oil",
            "2 cups Tomatoes",
            "5 Challots",
            "2 cloves Garlic",
            "Pinch Parsley",
            "1/2 kg chopped Chorizo"
          ]
        }
        """.trimIndent()
    ).toString()

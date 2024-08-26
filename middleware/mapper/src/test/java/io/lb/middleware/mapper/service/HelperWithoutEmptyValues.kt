package io.lb.middleware.mapper.service

internal fun expectedMappedResponse() =
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
            "2 cups",
            "5",
            "2 cloves",
            "Pinch",
            "1/2 kg chopped"
          ]
        }
        """.trimIndent()
    ).toString()

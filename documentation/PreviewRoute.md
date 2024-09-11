## Endpoint: Preview Mapping API

### Method: GET

**URL**: `https://projectmiddleware.fly.dev/v1/preview`

---

### Request:

**Headers**:
- `Content-Type`: `application/json`

**Request Body**:

Request body should contain the original response and the mapping rules. The original response should be an object containing the response from the API. The mapping rules should be an object containing the rules to map the original response to the new response.
More details on this can be found in the [Mapping Rules Documentation](MappingRules.md).

```json
{
  "originalResponse": {
    "meals": [
      {
        "idMeal": "53058",
        "strMeal": "Croatian Bean Stew",
        "strDrinkAlternate": null,
        "strCategory": "Beef",
        "strArea": "Croatian",
        "strInstructions": "Heat the oil in a pan. Add the chopped vegetables and sauté until tender. Take a pot, empty the beans together with the vegetables into it, put the sausages inside and cook for further 20 minutes on a low heat. Or, put it in an oven and bake it at a temperature of 180ºC/350ºF for 30 minutes. This dish is even better reheated the next day.",
        "strMealThumb": "https://www.themealdb.com/images/media/meals/tnwy8m1628770384.jpg",
        "strTags": "Warming, Soup, Beans",
        "strYoutube": "https://www.youtube.com/watch?v=mrjnQal3S1A",
        "strIngredient1": "Cannellini Beans",
        "strIngredient2": "Vegetable Oil",
        "strIngredient3": "Tomatoes",
        "strIngredient4": "Challots",
        "strIngredient5": "Garlic",
        "strIngredient6": "Parsley",
        "strIngredient7": "Chorizo",
        "strIngredient8": "",
        "strIngredient9": "",
        "strIngredient10": "",
        "strIngredient11": "",
        "strIngredient12": "",
        "strIngredient13": "",
        "strIngredient14": "",
        "strIngredient15": "",
        "strIngredient16": "",
        "strIngredient17": "",
        "strIngredient18": "",
        "strIngredient19": "",
        "strIngredient20": "",
        "strMeasure1": "2 cans",
        "strMeasure2": "3 tbs",
        "strMeasure3": "2 cups",
        "strMeasure4": "5",
        "strMeasure5": "2 cloves",
        "strMeasure6": "Pinch",
        "strMeasure7": "1/2 kg chopped",
        "strMeasure8": " ",
        "strMeasure9": " ",
        "strMeasure10": " ",
        "strMeasure11": " ",
        "strMeasure12": " ",
        "strMeasure13": " ",
        "strMeasure14": " ",
        "strMeasure15": " ",
        "strMeasure16": " ",
        "strMeasure17": " ",
        "strMeasure18": " ",
        "strMeasure19": " ",
        "strMeasure20": " ",
        "strSource": "https://www.visit-croatia.co.uk/croatian-cuisine/croatian-recipes/",
        "strImageSource": null,
        "strCreativeCommonsConfirmed": null,
        "dateModified": null
      }
    ]
  },
  "mappingRules": {
    "ignoreEmptyValues": true,
    "newBodyFields": {
      "idMeal": {
        "key": "id",
        "type": "Int"
      },
      "meal": {
        "key": "name",
        "type": "String"
      },
      "mealThumb": {
        "key": "thumbnail",
        "type": "String"
      },
      "measuredIngredients": {
        "key": "ingredients",
        "type": "String"
      }
    },
    "oldBodyFields": {
      "idMeal": {
        "keys": ["idMeal"],
        "type": "Int",
        "parents": ["meals"]
      },
      "meal": {
        "keys": ["strMeal"],
        "type": "String",
        "parents": ["meals"]
      },
      "mealThumb": {
        "keys": ["strMealThumb"],
        "type": "String",
        "parents": ["meals"]
      },
      "measuredIngredients": {
        "keys": [
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
        ],
        "type": "String",
        "parents": ["meals"]
      }
    }
  }
}
```

---

### Response:

**Status Code**: 200 OK

**Response Body** (Example):
```json
{
  "id": 53058,
  "name": "Croatian Bean Stew",
  "thumbnail": "https://www.themealdb.com/images/media/meals/tnwy8m1628770384.jpg",
  "ingredients": [
    "2 cans Cannellini Beans",
    "3 tbs Vegetable Oil",
    "2 cups Tomatoes",
    "5 Challots",
    "2 cloves Garlic",
    "Pinch Parsley",
    "1/2 kg chopped Chorizo"
  ]
}
```


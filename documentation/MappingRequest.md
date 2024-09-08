### Mapping Request Overview

This API demonstrates how the custom mapping functionality operates. It allows for transforming data from the original response into a new format based on a set of defined mapping rules, including field renaming, handling of nested structures, and concatenation.

### Mapping Rules Description:

For a detailed explanation of how the mapping rules work, please refer to the [Mapping Rules Overview](MappingRules.md).

### Example Mapping:

Here’s an example based on the mapping request:

```json
{
   "path":"getRandom",
   "method":"Get",
   "originalRoute":{
      "path":"api/json/v1/1/random.php",
      "method":"Get",
      "originalApi":{
         "baseUrl":"https://www.themealdb.com/"
      },
      "headers":{
         "Content-Type":"application/json;"
      }
   },
   "rulesAsString":{
      "ignoreEmptyValues":true,
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
      "oldBodyFields":{
         "idMeal":{
            "keys":[
               "idMeal"
            ],
            "type":"Int",
            "parents":[
               "meals"
            ]
         },
         "meal":{
            "keys":[
               "strMeal"
            ],
            "type":"String",
            "parents":[
               "meals"
            ]
         },
         "mealThumb":{
            "keys":[
               "strMealThumb"
            ],
            "type":"String",
            "parents":[
               "meals"
            ]
         },
         "measuredIngredients":{
            "keys":[
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
            "type":"String",
            "parents":[
               "meals"
            ]
         }
      }
   }
}
```

### API Response

Once the mapping process is complete, you can retrieve the mapped response using the following endpoint:

```bash
/v1/20bcc586-f47a-446a-bf23-02495f9fec55/getRandom
```

This will return the transformed response based on the mapping rules you provided. Below is an example of the mapped response:

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

### Notes:

- This API supports flexible mappings, including renaming fields, extracting data from nested objects, and concatenating multiple values into a single field (e.g., `ingredients`).
- Mappings can be fine-tuned to transform the structure of the original API response into the desired format. For more details, please check the [Mapping Rules Overview](MappingRules.md).
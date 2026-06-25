---
layout: default
title: Preview Route
parent: Getting Started
nav_order: 3
---

`POST /v1/preview` lets you test mapping rules against a sample response body **without** registering a permanent route or making any outbound calls. Use it to iterate on your `rulesAsString` until the output looks exactly right.

---

## Request

**`POST /v1/preview`**

```json
{
  "originalResponse": { ... },
  "mappingRules": { ... }
}
```

| Field | Description |
|-------|-------------|
| `originalResponse` | A sample JSON object representing the external API's response |
| `mappingRules` | The mapping rules to apply (same format as `rulesAsString` in the mapping request) |

---

## Full example

**Request:**

```json
{
  "originalResponse": {
    "meals": [
      {
        "idMeal": "53058",
        "strMeal": "Croatian Bean Stew",
        "strMealThumb": "https://www.themealdb.com/images/media/meals/tnwy8m1628770384.jpg",
        "strIngredient1": "Cannellini Beans",
        "strIngredient2": "Vegetable Oil",
        "strIngredient3": "Tomatoes",
        "strIngredient4": "Challots",
        "strIngredient5": "Garlic",
        "strIngredient6": "Parsley",
        "strIngredient7": "Chorizo",
        "strMeasure1": "2 cans",
        "strMeasure2": "3 tbs",
        "strMeasure3": "2 cups",
        "strMeasure4": "5",
        "strMeasure5": "2 cloves",
        "strMeasure6": "Pinch",
        "strMeasure7": "1/2 kg chopped"
      }
    ]
  },
  "mappingRules": {
    "ignoreEmptyValues": true,
    "newBodyFields": {
      "idMeal":              { "key": "id",         "type": "Int"    },
      "meal":                { "key": "name",        "type": "String" },
      "mealThumb":           { "key": "thumbnail",   "type": "String" },
      "measuredIngredients": { "key": "ingredients", "type": "String" }
    },
    "oldBodyFields": {
      "idMeal":    { "keys": ["idMeal"],      "type": "Int",    "parents": ["meals"] },
      "meal":      { "keys": ["strMeal"],     "type": "String", "parents": ["meals"] },
      "mealThumb": { "keys": ["strMealThumb"],"type": "String", "parents": ["meals"] },
      "measuredIngredients": {
        "keys": [
          "strMeasure1, strIngredient1", "strMeasure2, strIngredient2",
          "strMeasure3, strIngredient3", "strMeasure4, strIngredient4",
          "strMeasure5, strIngredient5", "strMeasure6, strIngredient6",
          "strMeasure7, strIngredient7"
        ],
        "type": "String",
        "parents": ["meals"]
      }
    }
  }
}
```

**Response `200 OK`:**

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

---

## Tips

- Use preview to verify `ignoreEmptyValues` behaves as expected before creating the route
- You can copy the `originalResponse` directly from a real API call to ensure the field names match exactly
- Once the preview output matches your expectations, use the same `mappingRules` as `rulesAsString` in [Mapping Request](../../mapping-request/)

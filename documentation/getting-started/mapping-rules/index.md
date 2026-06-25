---
layout: default
title: Mapping Rules
parent: Getting Started
nav_order: 2
---

Mapping rules define how the middleware transforms an external API's JSON response into your desired output shape. They are provided as the `rulesAsString` field when creating a mapped route.

---

## Top-level fields

| Field | Type | Description |
|-------|------|-------------|
| `ignoreEmptyValues` | `Boolean` | When `true`, fields with `null` or empty string values are omitted from the output |
| `newBodyFields` | `Object` | Declares the output fields: their names and types |
| `oldBodyFields` | `Object` | Declares how to read each value from the original response |

---

## `newBodyFields`

Each key in `newBodyFields` is the **identifier** used to link the rule to its corresponding entry in `oldBodyFields`. It is **not** the output field name.

```json
"newBodyFields": {
  "<rule-id>": {
    "key": "<output-field-name>",
    "type": "String" | "Int" | "Double" | "Boolean"
  }
}
```

---

## `oldBodyFields`

Each key must match a key in `newBodyFields`. It describes where to read the value from in the original response.

```json
"oldBodyFields": {
  "<rule-id>": {
    "keys": ["<source-field-name>"],
    "type": "String" | "Int" | "Double" | "Boolean",
    "parents": ["<parent-key>", "<nested-parent-key>"]
  }
}
```

| Property | Description |
|----------|-------------|
| `keys` | One or more source field names. Multiple keys are **concatenated** into a single output value (useful for ingredients + measures) |
| `type` | Expected value type for parsing and output |
| `parents` | Ordered list of object keys to traverse before reading `keys`. Leave empty `[]` for top-level fields |

---

## Multi-field concatenation

When `keys` contains multiple comma-separated pairs (e.g., `"strMeasure1, strIngredient1"`), the mapper reads both fields and joins them as a single string entry in an output array. This is ideal for combining measure and ingredient fields.

---

## Full example

Using `https://www.themealdb.com/api/json/v1/1/random.php` as the external API:

```json
{
  "rulesAsString": {
    "ignoreEmptyValues": true,
    "newBodyFields": {
      "idMeal":              { "key": "id",          "type": "Int"    },
      "meal":                { "key": "name",         "type": "String" },
      "mealThumb":           { "key": "thumbnail",    "type": "String" },
      "measuredIngredients": { "key": "ingredients",  "type": "String" }
    },
    "oldBodyFields": {
      "idMeal":    { "keys": ["idMeal"],      "type": "Int",    "parents": ["meals"] },
      "meal":      { "keys": ["strMeal"],     "type": "String", "parents": ["meals"] },
      "mealThumb": { "keys": ["strMealThumb"],"type": "String", "parents": ["meals"] },
      "measuredIngredients": {
        "keys": [
          "strMeasure1, strIngredient1",
          "strMeasure2, strIngredient2",
          "strMeasure3, strIngredient3",
          "strMeasure4, strIngredient4",
          "strMeasure5, strIngredient5",
          "strMeasure6, strIngredient6",
          "strMeasure7, strIngredient7"
        ],
        "type": "String",
        "parents": ["meals"]
      }
    }
  }
}
```

**Result:**

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

---
layout: page
title: Mapping Request
---

`POST /v1/mapping` registers a new mapped route. The middleware validates the mapping rules, makes a live test call to the external API, stores the route on success, and returns the UUID-based path to use for all future calls.

---

## Request body

```json
{
  "path": "<route-name>",
  "method": "Get" | "Post" | "Put" | "Delete" | "Patch" | "Head",
  "preConfiguredQueries": { "<key>": "<value>" },
  "preConfiguredHeaders": { "<key>": "<value>" },
  "preConfiguredBody": { "<key>": "<value>" },
  "originalRoute": {
    "path": "<external-api-path>",
    "method": "Get" | "Post" | "Put" | "Delete" | "Patch" | "Head",
    "originalApi": {
      "baseUrl": "https://..."
    },
    "authHeader": {
      "type": "None" | "Bearer" | "Basic",
      "token": "<your-token>"
    },
    "headers": { "<key>": "<value>" },
    "queries": { "<key>": "<value>" },
    "body": { "<key>": "<value>" }
  },
  "rulesAsString": { ... }
}
```

### Optional pre-configured fields

| Field | Description |
|-------|-------------|
| `preConfiguredQueries` | Query parameters sent with every request to the mapped route |
| `preConfiguredHeaders` | Headers forwarded on every outbound call to the external API |
| `preConfiguredBody` | Fixed request body sent on every outbound call |

---

## External API authentication

There are two ways to authenticate outbound calls to the external API — both independent from the Middleware's own `Authorization` header used by clients to authenticate with the Middleware itself.

### Option 1 — Pre-configured auth (stored with the route)

Include `authHeader` inside `originalRoute`. The token is stored and used automatically on every request to this mapped route.

```json
"originalRoute": {
  "path": "api/v1/resource",
  "method": "Get",
  "originalApi": { "baseUrl": "https://api.example.com/" },
  "authHeader": {
    "type": "Bearer",
    "token": "your-api-token"
  }
}
```

Supported types:

| Type | Outbound header sent |
|------|---------------------|
| `None` | The raw token value, no prefix |
| `Bearer` | `Authorization: Bearer <token>` |
| `Basic` | `Authorization: Basic <token>` |

### Option 2 — Runtime auth via `X-Mapped-Auth` (not stored)

Omit `authHeader` from `originalRoute` and pass the `X-Mapped-Auth` header on your requests instead. The token is never stored — every call must include it.

```
X-Mapped-Auth: Bearer your-api-token
X-Mapped-Auth: Basic dXNlcjpwYXNz
X-Mapped-Auth: raw-token
```

**When creating the route:** If `X-Mapped-Auth` is present and no `authHeader` is configured, the middleware uses it for the live validation call. The token is not persisted.

**When calling the mapped route:** The middleware applies `X-Mapped-Auth` to the outbound call if the route has no stored auth.

**Priority:** Pre-configured `authHeader` always takes precedence over `X-Mapped-Auth`.

> The Middleware's own `Authorization` header is never forwarded to the external API. `X-Mapped-Auth` is also never forwarded as-is — it is used internally to build the outbound `Authorization` header.

---

## Full example

```json
{
  "path": "getRandom",
  "method": "Get",
  "originalRoute": {
    "path": "api/json/v1/1/random.php",
    "method": "Get",
    "originalApi": {
      "baseUrl": "https://www.themealdb.com/"
    },
    "headers": {
      "Content-Type": "application/json"
    }
  },
  "rulesAsString": {
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

---

## Response

**`201 Created`** — returns the UUID path to use for future calls:

```
/v1/20bcc586-f47a-446a-bf23-02495f9fec55/getRandom
```

**Calling the mapped route:**

```
GET /v1/20bcc586-f47a-446a-bf23-02495f9fec55/getRandom
```

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

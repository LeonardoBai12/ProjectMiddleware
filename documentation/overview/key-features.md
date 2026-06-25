---
layout: default
title: Key Features
parent: How It Works
nav_order: 1
---

## Flexible field mapping

Rename any field from the external API response to a name that suits your domain. Define the source path (including nested parent traversal) and the target key, type, and position in the output.

```json
"newBodyFields": { "strMeal": { "key": "name", "type": "String" } },
"oldBodyFields": { "strMeal": { "keys": ["strMeal"], "parents": ["meals"], "type": "String" } }
```

---

## Nested structure extraction

Traverse deeply nested objects by listing parent keys in order. No custom query language — just a `parents` array that the mapper walks before reading the field value.

---

## Multi-field concatenation

Merge multiple source fields into a single output field. Useful for combining measure and ingredient pairs, full names, or any composite values from verbose API responses.

```json
"keys": ["strMeasure1, strIngredient1", "strMeasure2, strIngredient2"]
```

---

## Flexible authentication

Two options for authenticating outbound calls to the external API:

| Option | How | Stored? |
|--------|-----|---------|
| Pre-configured `authHeader` | Set `type` and `token` in the route body | Yes — used automatically on every request |
| Runtime `X-Mapped-Auth` header | Pass the full token on each request | No — client provides it every time |

Supported types: `Bearer`, `Basic`, `None`.

---

## Preview before committing

`POST /v1/preview` applies your mapping rules to a provided sample response without persisting anything. Verify the output before creating a permanent route.

---

## Live validation on route creation

When you register a route via `POST /v1/mapping`, the middleware makes a real call to the external API as part of the creation process. If the call fails, the route is not saved — so your registered routes are always known-good.

---

## UUID-based dynamic routing

Every registered route gets a unique UUID path (`/v1/{uuid}/{path}`). Routes are loaded from the database on startup, so they survive server restarts without re-registration.

---

## Clean Architecture internals

- **Use Case pattern** — each business operation is a self-contained callable class
- **Repository pattern** — all data access behind a single interface
- **Manual DI** — dependencies wired via `provideX()` module functions, no framework required
- **Reactive state machine** — `StateFlow` drives the middleware lifecycle (`Idle → Running → Stopped`)

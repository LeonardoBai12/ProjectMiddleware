---
layout: page
title: How It Works
---

ProjectMiddleware sits between your client application and any external API. You define the transformation rules once, and the middleware handles all subsequent calls — including authentication, field mapping, and response shaping.

---

## Request flow

```
Client → POST /v1/mapping        (register a route)
       ↓
Client → GET /v1/{uuid}/{path}   (call the mapped route)
       ↓
Middleware → External API        (live outbound call with stored config)
           → MapperService       (transforms the JSON response)
       ↓
Client ← Clean mapped JSON
```

---

## Step 1 — Define your mapping rules

Write a `rulesAsString` object that describes how to reshape the external API's response:

- **`ignoreEmptyValues`** — skip fields with null or empty values
- **`newBodyFields`** — target field names and types in the output
- **`oldBodyFields`** — source field paths, including nested parent traversal and multi-field concatenation

See [Mapping Rules](../getting-started/mapping-rules/) for the full reference.

---

## Step 2 — Preview before creating

Use `POST /v1/preview` to test your mapping rules against a sample response body **without** registering a permanent route. Iterate until the output looks right.

See [Preview Route](../getting-started/preview-route/) for examples.

---

## Step 3 — Create the mapped route

Send `POST /v1/mapping` with your route configuration. The middleware:

1. Validates the mapping rules
2. Makes a live test call to the external API (using your auth headers if provided)
3. Stores the route if the external call succeeds
4. Returns the UUID-based path you'll use for all future calls

See [Mapping Request](../getting-started/mapping-request/) for the full request format.

---

## Step 4 — Call your mapped route

```
GET /v1/{uuid}/{your-path}
```

The middleware calls the external API, applies the transformation, and returns the mapped JSON. The route remains live until explicitly removed.

If the route was created without a stored auth token, include `X-Mapped-Auth` on every request to authenticate the outbound call.

---

## Module architecture

```
middleware:core (entry point)
├── common:shared          — MiddlewareException, Resource<T>
├── common:data            — Core models and service interfaces
├── middleware:domain      — Use cases, state machine (Idle → Running → Stopped)
├── middleware:data        — Repository impl, orchestrates server/client/mapper/db
├── middleware:mapper      — JSON transformation engine
├── impl:ktor-server       — HTTP server routing (Ktor)
├── impl:ktor-client       — HTTP client + JWT token generation
└── impl:mongo-database    — MongoDB persistence for routes and APIs
```

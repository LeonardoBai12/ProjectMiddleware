---
layout: page
title: Endpoints Overview
---

All endpoints are served under the base URL of the deployed middleware instance. Authentication with the middleware itself uses HTTP Basic Auth on all routes.

---

## Route management

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/v1/mapping` | Register a new mapped route |
| `GET` | `/v1/routes` | List all registered mapped routes |
| `POST` | `/v1/preview` | Preview a mapping transformation without persisting |

---

## Dynamic mapped routes

| Method | Path | Description |
|--------|------|-------------|
| `GET` `POST` `PUT` `DELETE` `PATCH` `HEAD` | `/v1/{uuid}/{path}` | Call a registered mapped route |

The HTTP method of the mapped route is set at creation time and must match the method used when calling it.

---

## Authentication

### Middleware authentication (all endpoints)

All requests to the middleware require HTTP Basic Auth credentials.

```
Authorization: Basic <base64(username:password)>
```

### External API authentication (mapped routes only)

Two options for authenticating the middleware's outbound call to the original external API:

**Option 1 — Pre-configured (stored in route):**
Set `authHeader` in the route body at creation time. Handled automatically on every request.

**Option 2 — Runtime (`X-Mapped-Auth` header):**
Pass `X-Mapped-Auth: Bearer <token>` on each request to the mapped route. The middleware uses it for the outbound call without storing it.

```
X-Mapped-Auth: Bearer your-api-token
```

> The client's `Authorization` header is never forwarded to the external API.

---

## Hosted instance

The middleware is deployed at:

```
https://projectmiddleware.fly.dev
```

See the [Postman Collection](postman/) for ready-to-use examples against this instance.

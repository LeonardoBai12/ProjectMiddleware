---
layout: home
title: Home
nav_order: 1
---

**ProjectMiddleware** is a Kotlin/Ktor API transformation service. It acts as a middleware layer: clients register mapping rules that define how to call an external API and how to transform its JSON response. Mapped routes are then served dynamically via UUID-based paths.

**GitHub repository:** [github.com/LeonardoBai12/ProjectMiddleware](https://github.com/LeonardoBai12/ProjectMiddleware)

---

## What is it?

Instead of consuming a cluttered or deeply nested external API response directly, you register a *mapped route* that describes:

1. **Which external endpoint to call** — the base URL, path, method, headers, and optional auth
2. **How to transform the response** — field renaming, type conversion, value extraction from nested objects, and multi-field concatenation

Once registered, the middleware exposes a clean UUID-based endpoint. Every request to that endpoint triggers a live call to the original API and returns the transformed response.

---

## Quick navigation

| Section | Description |
|---------|-------------|
| [How It Works](overview/) | Step-by-step flow from client to mapped response |
| [Key Features](overview/key-features/) | Capabilities overview |
| [Mapping Rules](getting-started/mapping-rules/) | Rule syntax reference with full examples |
| [Mapping Request](getting-started/mapping-request/) | Route creation guide including authentication options |
| [Preview Route](getting-started/preview-route/) | Test your mapping rules before creating a route |
| [Endpoints](endpoints/) | All available HTTP endpoints |
| [Postman Collection](endpoints/postman/) | Interactive API collection |
| [KDoc Reference](api-reference/) | Auto-generated code documentation |
| [Ecosystem](ecosystem/) | Playground app, user service, and related repositories |

---

## The ecosystem

ProjectMiddleware is the backend of a broader ecosystem:

- **[Middleware Playground](https://github.com/LeonardoBai12/MiddlewarePlayground)** — a Kotlin Multiplatform app (Android + iOS) for creating and managing mapped routes with a visual editor
- **[Middleware User Service](https://github.com/LeonardoBai12/MIddlewareUserService)** — a dedicated Ktor authentication microservice that handles JWT issuance and user management for the ecosystem

See the [Ecosystem page](ecosystem/) for details.

---

## Built with

- [Kotlin](https://kotlinlang.org/) & [Ktor](https://ktor.io/) — server and HTTP client
- [MongoDB](https://www.mongodb.com/) — route and API persistence
- [Dokka](https://github.com/Kotlin/dokka) — KDoc generation
- [Jekyll](https://jekyllrb.com/) + [Chirpy](https://github.com/cotes2020/jekyll-theme-chirpy) — this documentation site

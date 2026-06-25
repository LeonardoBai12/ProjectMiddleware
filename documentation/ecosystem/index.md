---
layout: default
title: Ecosystem
nav_order: 5
---

ProjectMiddleware is the backend engine of a small ecosystem of services and apps designed to work together. Each piece has a single responsibility and evolves independently.

---

## Middleware Playground

[![GitHub](https://img.shields.io/badge/GitHub-MiddlewarePlayground-181717?logo=github)](https://github.com/LeonardoBai12/MiddlewarePlayground)

A **Kotlin Multiplatform** client application for Android and iOS that provides a visual interface for creating, previewing, and managing mapped routes — all powered by ProjectMiddleware on the backend.

![Playground icon]({{ site.baseurl }}/assets/images/middleware-playground-icon.png){: width="80" style="border-radius:12px; margin-bottom:0.5rem;"}

### What it does

- **Route management** — browse and manage all your registered mapped routes
- **Visual mapping editor** — step-by-step interface for configuring transformations: field mapping, concatenation, and complex JSON reshaping
- **Real-time preview** — test mappings against live data before committing
- **Integrated auth** — handles login and JWT token management via the User Service

### Tech stack

| Layer | Technology |
|-------|-----------|
| Shared logic | Kotlin Multiplatform |
| Android UI | Jetpack Compose |
| iOS UI | SwiftUI |
| HTTP client | Ktor Client (multiplatform) |
| Local persistence | SQLDelight |
| Async | Kotlin Coroutines & Flow |

### Architecture

Clean Architecture with SOLID principles throughout: separated domain, data, and presentation layers; event-based UI state management; Factory pattern for platform-specific implementations; and an Adapter pattern to share ViewModels between Android and iOS.

---

## Middleware User Service

[![GitHub](https://img.shields.io/badge/GitHub-MIddlewareUserService-181717?logo=github)](https://github.com/LeonardoBai12/MIddlewareUserService)

A dedicated **Kotlin/Ktor authentication microservice** that acts as the security layer for the entire ecosystem. By separating authentication into its own service, the main middleware can focus exclusively on API transformation.

### What it handles

- **User registration and login** — complete auth flows with BCrypt password hashing
- **JWT issuance and validation** — stateless, secure token management
- **Password policies** — minimum length, special character requirements
- **Input sanitization** — protection against injection and abuse
- **Token validation endpoint** — called by ProjectMiddleware before processing secured requests

### Tech stack

| Component | Technology |
|-----------|-----------|
| Runtime | Kotlin + Ktor |
| Database | MongoDB |
| Password hashing | BCrypt |
| Auth tokens | JWT |
| Async | Kotlin Coroutines & Flow |

---

## How the pieces fit together

```
Middleware Playground (Android / iOS)
        │
        │  registers routes, manages config
        ▼
ProjectMiddleware  ──── validates tokens ────▶  Middleware User Service
        │
        │  calls external APIs, transforms responses
        ▼
   Your custom JSON output
```

1. The **User Service** issues a JWT when the user signs in via the Playground
2. The **Playground** uses that token to authenticate with **ProjectMiddleware**
3. **ProjectMiddleware** validates the token, then calls the external API and maps the response
4. The **Playground** displays the transformed data

---

## Repository links

| Project | Repository |
|---------|-----------|
| ProjectMiddleware (this) | [github.com/LeonardoBai12/ProjectMiddleware](https://github.com/LeonardoBai12/ProjectMiddleware) |
| Middleware Playground | [github.com/LeonardoBai12/MiddlewarePlayground](https://github.com/LeonardoBai12/MiddlewarePlayground) |
| Middleware User Service | [github.com/LeonardoBai12/MIddlewareUserService](https://github.com/LeonardoBai12/MIddlewareUserService) |

---
layout: page
title: KDoc Reference
---

The KDoc reference is auto-generated from the source code on every push to `main` using [Dokka](https://github.com/Kotlin/dokka). It documents all public and internal declarations across every module.

---

## View the KDoc

[**Open KDoc Reference →**](../kdoc/index.html){:target="_blank"}

---

## Modules covered

| Module | Description |
|--------|-------------|
| `common:shared` | `MiddlewareException`, `Resource<T>` |
| `common:data` | Core models (`MappedRoute`, `OriginalRoute`, `MappedResponse`) and service interfaces |
| `middleware:domain` | Use cases, state machine, repository interface |
| `middleware:data` | `MiddlewareRepositoryImpl`, `MiddlewareDataSource` |
| `middleware:mapper` | JSON transformation engine |
| `impl:ktor-server` | HTTP server routing and `ServerServiceImpl` |
| `impl:ktor-client` | HTTP client, JWT token generation, `ClientServiceImpl` |
| `impl:mongo-database` | MongoDB persistence, `DatabaseServiceImpl` |

---

## How it stays up to date

The GitHub Actions workflow regenerates KDoc from the source on every push to `main`, then publishes the updated HTML alongside this site. No manual steps required.

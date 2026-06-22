# ProjectMiddleware

**ProjectMiddleware** is a Kotlin/Ktor API transformation service. Clients register mapping rules that define how to call an external API and how to transform its JSON response. Mapped routes are then served dynamically via UUID-based paths.

## Documentation

Full documentation is available at:

**[leonardobai12.github.io/ProjectMiddleware](https://leonardobai12.github.io/ProjectMiddleware/)**

| Section | Link |
|---------|------|
| How It Works | [Overview](https://leonardobai12.github.io/ProjectMiddleware/overview/) |
| Mapping Rules | [Getting Started](https://leonardobai12.github.io/ProjectMiddleware/getting-started/mapping-rules/) |
| Mapping Request | [Route Creation](https://leonardobai12.github.io/ProjectMiddleware/getting-started/mapping-request/) |
| Preview Route | [Preview](https://leonardobai12.github.io/ProjectMiddleware/getting-started/preview-route/) |
| Endpoints | [API Endpoints](https://leonardobai12.github.io/ProjectMiddleware/endpoints/) |
| Postman Collection | [Postman](https://leonardobai12.github.io/ProjectMiddleware/endpoints/postman/) |
| KDoc Reference | [API Reference](https://leonardobai12.github.io/ProjectMiddleware/api-reference/) |
| Ecosystem | [Ecosystem](https://leonardobai12.github.io/ProjectMiddleware/ecosystem/) |

## API Endpoints

### Base URL: `https://projectmiddleware.fly.dev/`

| Method | Path | Purpose |
|--------|------|---------|
| `POST` | `/v1/mapping` | Create a mapped route |
| `GET` | `/v1/routes` | List all mapped routes |
| `POST` | `/v1/preview` | Preview a mapped response |
| `*` | `/v1/{uuid}/{path}` | Serve a dynamically mapped route |

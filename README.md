# ProjectMiddleware

**ProjectMiddleware** is a Kotlin/Ktor API transformation service. It lets you define mapping rules that call an external API and reshape its JSON response — so every client gets exactly the format it needs, without touching the upstream service.

## Documentation

Full documentation: **[leonardobai12.github.io/ProjectMiddleware](https://leonardobai12.github.io/ProjectMiddleware/)**

## How It Works

1. **Define mapping rules** — specify which external endpoint to call and how to transform its response: rename fields, extract nested values, concatenate multiple fields, and more. See [Mapping Rules](https://leonardobai12.github.io/ProjectMiddleware/getting-started/mapping-rules/).

2. **Preview before committing** — call `/v1/preview` with your rules to verify the transformed output before creating a permanent route. See [Preview Route](https://leonardobai12.github.io/ProjectMiddleware/getting-started/preview-route/).

3. **Create the mapped route** — once the preview looks right, register the route. It gets a UUID-based path and stays live from that point on. See [Mapping Request](https://leonardobai12.github.io/ProjectMiddleware/getting-started/mapping-request/).

4. **Call the route** — send requests to `/v1/{uuid}/{path}` and receive the transformed response in real time.

## Key Features

- **Flexible field mapping** — rename fields, extract values from deeply nested structures, and concatenate multiple fields into one.
- **Preview before creation** — validate mappings against live data before committing to a permanent route.
- **Dynamic routing** — every registered route is served instantly via its UUID path with no redeployment needed.

## API Endpoints

**Base URL:** `https://projectmiddleware.fly.dev/`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/v1/mapping` | Create a mapped route |
| `GET` | `/v1/routes` | List all mapped routes |
| `POST` | `/v1/preview` | Preview a mapped response |
| `*` | `/v1/{uuid}/{path}` | Call a mapped route |

## Postman Collection

For a complete list of routes and ready-to-run examples, see the [Postman collection](https://documenter.getpostman.com/view/28162587/2sAXjRX9p1#intro).

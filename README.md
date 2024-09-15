# Project Middleware Overview

Welcome to the **Project Middleware**! This API allows you to transform data from any public API to a format that best fits your needs. The process involves defining mapping rules to convert fields, handle nested structures, and concatenate values where necessary.

## How It Works

1. **Define Mapping Rules**: Use the provided rules in [Documentation for Mapping Rules](./documentation/MappingRules.md) to create custom mapping logic for your API responses. You can transform field names, exclude empty values, and more.

2. **Test with Preview Route**: Before creating a permanent mapping route, it's recommended to test your configuration using the **Preview Route**. This ensures the mapped response matches your expectations without committing to a permanent route.

   - For more information, see [Documentation for Preview Route](./documentation/PreviewRoute.md).

3. **Create a Mapped Route**: Once you’re satisfied with the preview response, you can proceed to create the actual mapped route using the **Mapping Request** guidelines.

   - Learn more about this in [Documentation for Mapping Request](./documentation/MappingRequest.md).

## Key Features

- **Flexible Mapping**: You can rename fields, extract values from deeply nested structures, and concatenate multiple fields into one.
- **Preview Before Creation**: Use the preview functionality to verify your mappings before committing to a route.
- **Easy Field Transformation**: Transform field names and types seamlessly with custom rules.

## Steps to Create a Mapped Route

1. **Preview the Response**:
   - Call the `/v1/preview` endpoint with your rules to ensure that the response matches your expected output. This step is crucial before proceeding to create a permanent mapped route.
   - [Documentation for Mapping Rules](./documentation/MappingRules.md): Detailed explanation of how to define your mapping rules.
   - [Documentation for Preview Route](./documentation/PreviewRoute.md): Guidelines for testing your mappings.

2. **Create the Mapped Route**:
   - After confirming the previewed response, create the route using the mapping request as described in these docs:
   - [Documentation for Mapping Request](./documentation/MappingRequest.md): Full instructions on how to set up your mapped route.

3. **Call the Mapped Route**:
   - Once the route is created, you can start using the mapped route to get transformed responses by calling `/v1/{uuid}/{path}`.

## API Endpoints Overview

### Base URL: https://projectmiddleware.fly.dev/

- **`GET https://projectmiddleware.fly.dev/v1/preview`**: Preview a mapped response before creating the route.
- **`GET https://projectmiddleware.fly.dev/v1/routes`**: Retrieve all mapped routes.
- **`POST https://projectmiddleware.fly.dev/v1/mapping`**: Define a new mapping rule for an API.
- **`GET https://projectmiddleware.fly.dev/v1/{uuid}/{path}`**: Retrieve the mapped response using the specific UUID and path.

## Postman Documentation

### [Postman Collection URL](https://documenter.getpostman.com/view/28162587/2sAXjRX9p1#intro)
For a complete list of routes, examples, and how to use the API, visit our Postman collection. 

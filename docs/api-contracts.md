# Microservices API Contracts

## 1. Product Service

Base path: /api/v1/products

### Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | /api/v1/products | Retrieve products |
| GET | /api/v1/products/{id} | Retrieve a product by ID |
| POST | /api/v1/products | Create a product |
| PUT | /api/v1/products/{id} | Replace a product |
| PATCH | /api/v1/products/{id} | Partially update a product |
| DELETE | /api/v1/products/{id} | Delete a product |

### Request validation

- Product name is required.
- Product name must contain 2–100 characters.
- Product description cannot exceed 500 characters.
- Product price must be zero or greater.
- Product category is required and must contain 2–50 characters.
- Product stock must be zero or greater.

### Response codes

- 200: Successful retrieval or update.
- 201: Product created.
- 204: Product deleted.
- 400: Invalid request.
- 401: Authentication required.
- 403: Access forbidden.
- 404: Product not found.
- 500: Unexpected server error.

## 2. API Versioning

The Product Service uses URL-based versioning:

/api/v1/products

## 3. API Documentation

OpenAPI specification:

/v3/api-docs

Swagger UI:

/swagger-ui/index.html
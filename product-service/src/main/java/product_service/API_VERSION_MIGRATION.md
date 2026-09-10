Product API Versioning Documentation

## Overview

The Product Service supports two API versions:

- V1 — Existing/old API
- V2 — New API with additional product information

Both versions are available independently.

---

# V1 API

## Endpoint

GET /api/v1/products

## Example

http://localhost:8082/api/v1/products

## V1 Response

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 50000
}
V1 represents the existing API contract used by older clients.
V2 API
Endpoint
GET /api/v2/products
Example
http://localhost:8082/api/v2/products⁠�
V2 Response
{
  "id": 1,
  "name": "Laptop",
  "price": 50000,
  "category": "Electronics",
  "stock": 20,
  "status": "AVAILABLE"
}
V2 provides additional product information.
Breaking Changes
The V2 API changes the response structure compared with V1.
V1
V1 provides basic product information:
id
name
price
V2
V2 provides additional information:
id
name
price
category
stock
status
The additional fields allow clients to use more product information.
Backward Compatibility
The existing V1 API remains available.
Older clients can continue using:
GET /api/v1/products
New clients can use:
GET /api/v2/products
Therefore, introducing V2 does not require existing V1 clients to immediately migrate.
Versioning Strategy
This project uses URL-based API versioning.
Examples:
V1: GET /api/v1/products
V2: GET /api/v2/products
URL versioning is simple, clear, and easy for clients to understand.
Summary
Version
Endpoint
Purpose
V1
/api/v1/products
Existing API
V2
/api/v2/products
New API with additional fields
Both APIs can operate independently.
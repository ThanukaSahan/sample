# Sample Spring Boot CRUD Application

Simple Spring Boot application demonstrating CRUD operations on a `Product` entity backed by H2 in-memory DB.

Build and run:

```bash
mvn spring-boot:run
```

API endpoints (base `/api/products`):

- `GET /api/products` — list all products
- `GET /api/products/{id}` — get product by id
- `POST /api/products` — create product (JSON body)
- `PUT /api/products/{id}` — update product (JSON body)
- `DELETE /api/products/{id}` — delete product

Example create request:

```bash
curl -X POST -H "Content-Type: application/json" -d '{"name":"Widget","description":"A test widget","price":9.99}' http://localhost:8080/api/products
```

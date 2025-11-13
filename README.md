# Book Shopping Application

A Spring Boot 3.5 backend for a feature-rich books e-commerce platform, built with Java 17, PostgreSQL, JPA/Hibernate, and Flyway migrations.

## Features

- Book catalog with search, filter, sort, and pagination
- Cart and wishlist management
- Category hierarchy with admin endpoints
- User authentication and JWT foundation
- Global exception handling and bean validation
- PostgreSQL database, Flyway migrations for schema management
- Monitoring with Spring Boot Actuator and Prometheus

## Tech Stack

- Java 17
- Spring Boot 3.5.6
- Spring Web, Spring Data JPA, Spring Security, Validation
- PostgreSQL, HikariCP
- Flyway, Testcontainers (test profile)
- JJWT (JWT support)
- Spring Boot Actuator, Prometheus metrics

## Getting Started

1. **Clone the repository:**

2. **Set up PostgreSQL:**
- Create a database named `bookshop`
- Create a user with privileges

3. **Configure application properties:**
Create `src/main/resources/application.yml`:

4. **Run the application:**
- On Unix/macOS:
  ```
  ./mvnw spring-boot:run
  ```
- On Windows:
  ```
  mvnw.cmd spring-boot:run
  ```
The API server starts at `http://localhost:8080`.

## API Endpoints (Sample)

- **Books**
- `GET /api/books`
- `GET /api/books/{id}`
- `POST /api/books` (admin)
- `PUT /api/books/{id}` (admin)
- `DELETE /api/books/{id}` (admin)
- **Categories**
- `GET /api/categories/tree`
- `POST /api/categories` (admin)
- **Cart**
- `GET /api/cart`
- `POST /api/cart/items`
- **Wishlist**
- `GET /api/wishlist`
- `POST /api/wishlist/items`
- **Auth**
- `POST /api/auth/register`
- `POST /api/auth/login`

## Build and Test


## Development Notes

- Use devtools for hot reload in development.
- Keep business logic in services.
- Write migration scripts under `src/main/resources/db/migration`.

## Roadmap

- Swagger/OpenAPI documentation
- JWT authentication for all protected endpoints
- Expanded admin features and auditing
- Recommendations and review modules


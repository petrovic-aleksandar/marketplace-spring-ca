src/main/java/me/aco/marketplace_spring_ca/

# Marketplace Spring Pragmatic Clean Architecture

## Architecture Overview

This project uses a **pragmatic Clean Architecture** where domain and persistence (JPA) entities are merged. This reduces boilerplate and complexity, while still maintaining clear separation of concerns for business logic, application orchestration, and external interfaces.

### Layer Structure

```
src/main/java/me/aco/marketplace_spring_ca/
├── domain/                      # Business logic & entities (JPA-annotated)
│   ├── entities/               # Domain entities (also JPA entities)
│   └── enums/                  # Domain enums
│   └── intefrace/              # Domain interfaces (e.g., repository ports)
│
├── application/                # Application business rules
│   ├── dto/                    # Data Transfer Objects
│   ├── exceptions/             # Application exceptions
│   └── usecases/               # Use cases / Application services
│
├── infrastructure/             # External concerns
│   ├── config/                 # Spring and app config
│   ├── file/                   # File storage, etc.
│   ├── persistence/            # Spring Data JPA repositories (may be thin wrappers)
│   └── security/               # Security config
│
└── presentation/               # API layer
   ├── controllers/            # REST controllers
   └── error/                  # Error handling
```

## Dependency Flow

```
Presentation → Application → Domain
Infrastructure → Domain
```

- **Domain**: Core business logic, entities with JPA annotations, repository interfaces (ports)
- **Application**: Use cases, DTOs, orchestrates domain logic
- **Infrastructure**: Implements repository interfaces using Spring Data JPA, config, file, security
- **Presentation**: REST controllers, error handling



## Command/Query Separation (CQS)

The application layer follows a clear **Command/Query Separation** pattern:

- **Command Handlers**: Responsible for operations that modify state (create, update, delete). Each command handler is annotated with `@Transactional` to ensure atomicity and consistency of changes.
- **Query Handlers**: Responsible for read-only operations that fetch data. Query handlers may be annotated with `@Transactional(readOnly = true)` to optimize performance and signal intent, but do not modify state. 
   - **Note:** Query handlers can use faster Spring Data CRUD repositories (such as `CrudRepository` or `PagingAndSortingRepository`) instead of full JPA repositories when only simple read operations are needed. This can improve performance for queries that do not require complex JPA features.

This separation improves maintainability, clarity, and testability. It also allows for more granular transaction management and aligns with best practices for scalable application design.

## Design Decisions

### 1. **Merged Domain and JPA Entities**
   - Domain entities are also JPA entities (with annotations)
   - **Pros:**
     - Reduced complexity and boilerplate
     - No need for entity-to-model mapping
     - Simpler codebase for most business apps
   - **Cons:**
     - Domain is coupled to JPA (harder to swap persistence tech)
   - **Trade-off:** Simplicity and maintainability are prioritized over pure decoupling

### 2. **Repository Interfaces in Domain**
   - Domain layer defines repository interfaces (ports)
   - Infrastructure implements these interfaces using Spring Data JPA
   - Application and presentation layers depend only on domain interfaces

### 3. **Use Case Services in Application Layer**
   - Application layer contains use case services
   - Orchestrates domain logic and repositories
   - Handles transactions and business workflows

### 4. **DTOs for API Boundaries**
   - Application layer defines DTOs for requests/responses
   - Prevents domain entities from leaking to API
   - Allows API evolution without breaking domain logic


## Key Features

### Domain Logic in Entities
Domain entities encapsulate business rules and are directly annotated as JPA entities. Example:
```java
@Entity
public class Product {
   // ... fields ...
   public void reduceStock(int quantity) {
      if (quantity > stockQuantity) {
         throw new IllegalArgumentException("Insufficient stock");
      }
      this.stockQuantity -= quantity;
   }
}
```

### Transaction Management
Use case services in the application layer are annotated with `@Transactional` to ensure business operations are atomic:
```java
@Service
@Transactional
public class ProductService { ... }
```

### Global Exception Handling
Centralized error handling is provided in `GlobalExceptionHandler` in the presentation layer.

## Running the Application

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access H2 Console:**
   - URL: http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:mem:marketplacedb
   - Username: sa
   - Password: (empty)

## API Endpoints

### Users
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `DELETE /api/users/{id}` - Delete user

### Products
- `POST /api/products` - Create product
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/seller/{sellerId}` - Get products by seller
- `GET /api/products/search?name={name}` - Search products
- `PUT /api/products/{id}` - Update product
- `PATCH /api/products/{id}/stock?quantity={qty}` - Update stock
- `DELETE /api/products/{id}` - Delete product

## Example Requests

### Create User
```json
POST /api/users
{
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "role": "SELLER"
}
```

### Create Product
```json
POST /api/products
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 1200.00,
  "stockQuantity": 10,
  "sellerId": 1
}
```

## Testing

Run tests with:
```bash
mvn test
```

## Future Enhancements

- Add authentication & authorization (Spring Security)
- Implement password hashing
- Add validation annotations
- Add pagination for list endpoints
- Add API documentation (OpenAPI/Swagger)
- Add integration tests
- Add audit logging

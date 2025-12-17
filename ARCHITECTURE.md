# Marketplace Spring Clean Architecture

## Architecture Overview

This project implements a **pragmatic Clean Architecture** approach where JPA entities are merged with the domain layer to reduce complexity while maintaining separation of concerns.

### Layer Structure

```
src/main/java/me/aco/marketplace_spring_ca/
├── domain/                      # Business logic & entities (with JPA annotations)
│   ├── entities/               # Domain entities (also JPA entities)
│   │   ├── Product.java
│   │   └── User.java
│   └── repositories/           # Repository interfaces (ports)
│       ├── ProductRepository.java
│       └── UserRepository.java
│
├── application/                # Application business rules
│   ├── dto/                    # Data Transfer Objects
│   │   ├── CreateProductRequest.java
│   │   ├── UpdateProductRequest.java
│   │   ├── ProductResponse.java
│   │   ├── CreateUserRequest.java
│   │   └── UserResponse.java
│   ├── exceptions/             # Application exceptions
│   │   ├── BusinessException.java
│   │   └── ResourceNotFoundException.java
│   └── usecases/              # Use cases / Application services
│       ├── ProductService.java
│       └── UserService.java
│
├── infrastructure/             # External concerns
│   ├── persistence/           # JPA repositories
│   │   ├── JpaProductRepository.java
│   │   └── JpaUserRepository.java
│   └── adapters/              # Repository adapters
│       ├── ProductRepositoryAdapter.java
│       └── UserRepositoryAdapter.java
│
└── presentation/              # API layer
    ├── controllers/           # REST controllers
    │   ├── ProductController.java
    │   └── UserController.java
    └── error/                 # Error handling
        ├── ErrorResponse.java
        └── GlobalExceptionHandler.java
```

## Dependency Flow

```
Presentation → Application → Domain ← Infrastructure
```

- **Domain**: Core business logic, entities with JPA annotations, repository interfaces
- **Application**: Use cases, DTOs, orchestrates domain logic
- **Infrastructure**: Implements repository interfaces using Spring Data JPA
- **Presentation**: REST controllers, error handling

## Design Decisions

### 1. **Merged Domain and Persistence Layer**
   - JPA entities are in the domain layer with annotations
   - **Pros**: 
     - Reduced complexity
     - Less boilerplate code
     - No need for entity-to-model mapping
   - **Cons**: 
     - Domain depends on JPA framework
     - Harder to switch persistence technologies
   - **Trade-off accepted**: For most applications, the simplicity gain outweighs the coupling

### 2. **Repository Pattern**
   - Domain defines repository interfaces (ports)
   - Infrastructure provides implementations (adapters)
   - Application layer depends on domain interfaces only

### 3. **Use Case Services**
   - Each service represents application use cases
   - Orchestrates domain entities and repositories
   - Handles transactions

### 4. **DTOs in Application Layer**
   - Separate request/response DTOs
   - Prevents domain entities from leaking to presentation
   - Allows API evolution independent of domain

## Key Features

### Domain Logic in Entities
Entities contain business rules:
```java
public void reduceStock(int quantity) {
    if (quantity > stockQuantity) {
        throw new IllegalArgumentException("Insufficient stock");
    }
    this.stockQuantity -= quantity;
}
```

### Transaction Management
Use cases are annotated with `@Transactional`:
```java
@Service
@Transactional
public class ProductService { ... }
```

### Global Exception Handling
Centralized error handling in `GlobalExceptionHandler`

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

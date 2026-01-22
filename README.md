# Book Management API

A comprehensive RESTful API built with Spring Boot 3 for managing books, authors, categories, users, and reviews with Role-Based Access Control (RBAC).

## 🚀 Features

- **Complete CRUD Operations** for Books, Authors, Categories, Users, and Reviews
- **Role-Based Access Control (RBAC)** with USER and ADMIN roles
- **JWT Authentication** for secure API access
- **Advanced Book Filtering** by author, category, rating, and publication date
- **Pagination & Sorting** support
- **Custom Annotations** for validation and security
- **ISBN Validation** supporting both ISBN-10 and ISBN-13 formats
- **Automatic Rating Calculation** from reviews
- **Comprehensive Error Handling** with meaningful messages
- **API Documentation** with Swagger/OpenAPI
- **Data Integrity** enforcement through database constraints and business rules

## 🛠️ Technology Stack

- **Java 21**
- **Spring Boot 3.2.1**
- **Spring Security** with JWT
- **Spring Data JPA**
- **H2 Database** (development) / PostgreSQL (production-ready)
- **MapStruct** for DTO mapping
- **Lombok** for boilerplate reduction
- **JUnit 5** for testing
- **Springdoc OpenAPI** for API documentation

## 📋 Prerequisites

- JDK 21 or higher
- Maven 3.8+
- Git

## 🔧 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd book-management-api
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access H2 Console (Development)
To Run on Test for a quick setup without setting up database:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:bookdb`
- Username: `sa`
- Password: (leave blank)

### 5. Access API Documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/api-docs`

## 🔐 Authentication

The API uses JWT-based authentication. To access protected endpoints:

### 1. Create a User (Registration)

```bash
POST /api/users
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "role": "USER"
}
```

### 2. Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "USER"
  }
}
```

### 3. Use the Token

Include the token in the Authorization header for all subsequent requests:

```bash
Authorization: Bearer <your-token-here>
```

## 📚 API Endpoints

### Books

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/books` | Get all books (with filters) | USER, ADMIN |
| GET | `/api/books/{id}` | Get book by ID | USER, ADMIN |
| POST | `/api/books` | Create new book | ADMIN |
| PUT | `/api/books/{id}` | Update book | ADMIN |
| DELETE | `/api/books/{id}` | Delete book | ADMIN |

**Query Parameters for GET /api/books:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)
- `authorId` - Filter by author
- `categoryId` - Filter by category
- `ratingMin` - Minimum rating
- `ratingMax` - Maximum rating
- `publishedStart` - Published after date (ISO format)
- `publishedEnd` - Published before date (ISO format)
- `sortBy` - Sort field (default: id)

### Authors

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/authors` | Get all authors | USER, ADMIN |
| GET | `/api/authors/{id}` | Get author by ID | USER, ADMIN |
| POST | `/api/authors` | Create new author | ADMIN |
| PUT | `/api/authors/{id}` | Update author | ADMIN |
| DELETE | `/api/authors/{id}` | Delete author | ADMIN |

### Categories

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/categories` | Get all categories | USER, ADMIN |
| POST | `/api/categories` | Create new category | ADMIN |
| PUT | `/api/categories/{id}` | Update category | ADMIN |
| DELETE | `/api/categories/{id}` | Delete category | ADMIN |

### Users

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/users` | Register new user | Public |
| GET | `/api/users/{id}` | Get user by ID | USER, ADMIN |
| PUT | `/api/users/{id}` | Update user | USER, ADMIN |
| DELETE | `/api/users/{id}` | Delete user | USER, ADMIN |

### Reviews

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/books/{bookId}/reviews` | Create review | USER, ADMIN |
| GET | `/api/books/{bookId}/reviews` | Get all reviews for book | USER, ADMIN |
| PUT | `/api/reviews/{id}` | Update review | USER, ADMIN |
| DELETE | `/api/reviews/{id}` | Delete review | USER, ADMIN |

## 📝 Request/Response Examples

### Create a Book

**Request:**
```bash
POST /api/books
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "title": "Clean Code",
  "isbn": "978-0132350884",
  "publishedDate": "2008-08-01",
  "authorId": 1,
  "categoryIds": [1, 2]
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Clean Code",
  "isbn": "978-0132350884",
  "publishedDate": "2008-08-01",
  "author": {
    "id": 1,
    "name": "Robert C. Martin"
  },
  "categories": [
    {"id": 1, "name": "Programming"},
    {"id": 2, "name": "Software Engineering"}
  ],
  "rating": 0.0
}
```

### Create a Review

**Request:**
```bash
POST /api/books/1/reviews
Authorization: Bearer <user-token>
Content-Type: application/json

{
  "rating": 5,
  "comment": "Excellent book! A must-read for developers.",
  "userId": 1
}
```

**Response:**
```json
{
  "id": 1,
  "book": {
    "id": 1,
    "title": "Clean Code",
    "isbn": "978-0132350884",
    "publishedDate": "2008-08-01"
  },
  "user": {
    "id": 1,
    "username": "testuser"
  },
  "rating": 5,
  "comment": "Excellent book! A must-read for developers.",
  "createdAt": "2026-01-20T10:30:00"
}
```

## 🏗️ Architecture & Design

### Project Structure

```
src/main/java/com/bookmanagement/
├── annotation/         # Custom annotations (@ValidISBN, @AdminOnly, etc.)
├── config/             # Configuration classes (Security, OpenAPI)
├── controller/         # REST controllers
├── dto/                # Data Transfer Objects
├── entity/             # JPA entities
├── exception/          # Custom exceptions and global handler
├── mapper/             # MapStruct mappers
├── repository/         # Spring Data JPA repositories
├── security/           # JWT and security components
├── service/            # Business logic layer
├── specification/      # JPA Specifications for filtering
├── utils/              # contains utility methods
├── constants/          # contains the application constants
└── validator/          # Custom validators
```

### Design Patterns Used

1. **Repository Pattern** - Data access abstraction
2. **Service Layer Pattern** - Business logic separation
3. **DTO Pattern** - Decoupling entities from API responses
4. **Specification Pattern** - Dynamic query building
5. **Strategy Pattern** - Role-based authorization

### Custom Annotations

- `@ValidISBN` - Validates ISBN-10 and ISBN-13 formats
- `@AdminOnly` - Restricts access to ADMIN role
- `@UserOrAdmin` - Allows USER and ADMIN roles

### Business Rules Implementation

1. **Books must have at least one category and an author**
   - Enforced in `BookService.createBook()` with validation
   
2. **ISBNs must be unique and validated**
   - Database constraint + custom `@ValidISBN` annotation
   
3. **Book ratings are auto-calculated from reviews**
   - Implemented in `Book.calculateRating()` method
   - Triggered after review creation/update/deletion
   
4. **Role-based access control**
   - Custom annotations `@AdminOnly` and `@UserOrAdmin`
   - JWT-based authentication with Spring Security

## 🔒 Security Features

1. **JWT Authentication** - Stateless token-based auth
2. **Password Encryption** - BCrypt hashing
3. **Role-Based Access Control** - ADMIN and USER roles
4. **Method-Level Security** - @PreAuthorize annotations
5. **CORS Configuration** - Configurable for production

## 📊 Database Schema

### Tables

- `users` - User accounts with roles
- `authors` - Book authors
- `books` - Book information
- `categories` - Book categories
- `book_categories` - Many-to-many relationship
- `reviews` - User reviews for books
- `roles` - users roles e.g ADMIN etc

### Relationships

- Author → Books (One-to-Many)
- Book → Categories (Many-to-Many)
- Book → Reviews (One-to-Many)
- User → Reviews (One-to-Many)
- Author -> Role (Many-to-Many)

## ⚙️ Configuration

### Application Properties

Key configuration in `application-test.properties`:

```bash
spring.datasource.url=jdbc:h2:mem:bookdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

### Production Configuration

For production deployment:

1. Switch to PostgreSQL in application-prod.properties:
```bash
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:bookdb}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

2. Use environment variables for sensitive data
3. Enable HTTPS
4. Configure CORS for your frontend domain



# 📦 Solution Architecture & Design Overview

This document explains the architectural decisions, security model, development workflow, validation rules, and test coverage strategy used in building the application.
Based on the role based requirement from the document a login endpoint is required in my approach.
endpoint: api/auth/login

---

## 📌 1. Identified Requirements & Missing Features

During initial analysis, it was observed that the documentation did not provide a way for newly created users to authenticate. To address this gap, the following additions were made:

- Introduced a **login endpoint** for both regular and admin users.
- Added **JWT-based authentication**.
- Implemented **role-based authorization** for secured endpoints.
- Used the `create-user` endpoint as the **registration path** for new users.

Users authenticate with credentials and are authorized based on their assigned roles stored in the JWT.

---

## 🚀 2. System Modeling & Bootstrapping Process

**Approach:**

1. **Entity Modeling**
   - Designed entities using an ERD.
   - Determined relationships and domain boundaries.

2. **Service & Utility Identification**
   - Outlined required services, utilities, and custom logic ahead of implementation.

3. **Project Bootstrapping**
   - Project generated using **Spring Initializr**.
   - Development followed the pattern:



SOLID principles were observed to ensure maintainability and low coupling.

---

## 🔐 3. Authentication & Authorization Mechanism

### **Authentication**
Implemented using **JWT tokens**, where:

- User details + roles are encoded into the token.
- Tokens are sent on every secured request.
- Stateless design eliminates session persistence.

### **Authorization**
Handled through:

- Method-level security with custom annotations.
- Role checks extracted from JWT during request filtering.

### **Access Control Rules**

| Endpoint | Access |
|---|---|
| `POST /users` | Public (`permitAll`) |
| `POST /login` | Public (`permitAll`) |
| Admin Operations | `ADMIN` only |
| General Operations | `USER` or `ADMIN` |

---

## 🧩 4. Role Management & Constraints

### **Role Entity**
Instead of enums, roles were modeled as a **Role Entity** to support future scalability and dynamic role creation.

### **Base Roles**
Two base roles are seeded:

- `ADMIN`
- `USER`

### **Constraints Applied**

- Default `ADMIN` is created automatically at startup.
- Users cannot self-register as `ADMIN`.
- Only admins can assign or update elevated roles.
- If a non-existing role is provided during sign-up, system assigns the default `USER` role.

**Startup Logs:**
- Username: admin
- Email: admin@bookmanagement.com
- Password: admin123$
⚠️ IMPORTANT: Change this password immediately!

---

## 🛡 5. Validation Layer

### **Password Validation**
Validation implemented using a custom annotation enforcing:

- Minimum 6 characters
- At least 1 digit
- At least 1 special character

### **ISBN Validation**
Included ISBN utility with proper formatting, e.g:


---

## 🔑 6. Login Endpoint :api/auth/login

A dedicated login endpoint was implemented to:

- Authenticate both user types
- Return JWT token
- Support authorization flow for secured resources

---

## 🧱 7. Security Behavior Notes

Spring Security may still attempt to parse JWT tokens on endpoints that are marked as permitAll. This happens when a JWT is included in the request header, causing the filter chain to validate the token. If the token is expired, it breaks the request flow even though the endpoint itself is meant to be public, because permitAll does not prevent the request from passing through the security filters.
To mitigate this, those endpoints were explicitly excluded from the JWT filter so that expired or invalid tokens do not affect public access or break the application.

---

## 📚 8. Dynamic Book Search via Specification and Automatic Rate review.

Dynamic filtering of books was implemented using **Spring JPA Specifications**:

> Provides flexible criteria-based searching without breaking when parameters are missing.

This pattern is commonly recommended for flexible filtering in JPA.
> Book Ratings a recalculating for every new or updated review before insertion to db.

---

## 🧪 9. Test Coverage & Quality Assurance

**Tests written for:**

- Validation utilities (ISBN)
- Service layer behaviors
## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

**Coverage tracked via JaCoCo:**

### 🧪 Test Coverage (JaCoCo)

To run tests and generate the coverage report:

```bash
mvn clean test jacoco:report
target/site/jacoco/index.html
- for MacOS
open target/site/jacoco/index.html
- for windows
start target/site/jacoco/index.html





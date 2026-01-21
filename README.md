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

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

The project includes comprehensive unit tests for:
- Services (business logic)
- Controllers (API endpoints)
- Validators (ISBN validation)
- Repositories (data access)

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

### Relationships

- Author → Books (One-to-Many)
- Book → Categories (Many-to-Many)
- Book → Reviews (One-to-Many)
- User → Reviews (One-to-Many)

## ⚙️ Configuration

### Application Properties

Key configuration in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:bookdb  # Change for production
  jpa:
    hibernate:
      ddl-auto: create-drop  # Change to 'validate' in production

application:
  security:
    jwt:
      secret-key: <your-secret>
      expiration: 86400000  # 24 hours
```

### Production Configuration

For production deployment:

1. Switch to PostgreSQL:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookdb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
```

2. Use environment variables for sensitive data
3. Enable HTTPS
4. Configure CORS for your frontend domain

## 🚀 Deployment

### Build for Production

```bash
mvn clean package -DskipTests
```

The executable JAR will be in `target/book-management-api-1.0.0.jar`

### Run Production Build

```bash
java -jar target/book-management-api-1.0.0.jar
```

### Docker Deployment (Optional)

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/book-management-api-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🐛 Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   - Change port in `application.yml`: `server.port: 8081`

2. **JWT token expired**
   - Login again to get a new token

3. **403 Forbidden**
   - Ensure you're using the correct role (ADMIN for management endpoints)

## 📖 Design Decisions & Assumptions

### Design Decisions

1. **H2 In-Memory Database** - Chosen for easy setup and testing. Switch to PostgreSQL for production.

2. **JWT Authentication** - Stateless authentication suitable for RESTful APIs and microservices.

3. **MapStruct for DTO Mapping** - Compile-time mapping for better performance and type safety.

4. **Custom Annotations** - Improves code readability and reduces boilerplate for validation and security.

5. **Specification Pattern** - Enables dynamic query building for complex filtering without query method explosion.

### Assumptions

1. **Single User Review per Book** - Not enforced but could be added as a business rule.

2. **Soft Deletes Not Implemented** - Hard deletes are used; implement soft deletes for audit requirements.

3. **Review Rating Range** - Ratings are 1-5 stars (validated).

4. **Email Uniqueness** - Both users and authors must have unique emails.

5. **Category Assignment** - Books can have multiple categories but must have at least one.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Spring Boot Documentation
- Baeldung Spring Security Tutorials
- Clean Code by Robert C. Martin

---

**Note:** This is a production-ready API implementation with comprehensive error handling, validation, security, and testing. For any questions or issues, please open an issue on GitHub.
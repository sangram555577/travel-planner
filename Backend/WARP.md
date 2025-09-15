# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

**TripCraft** (also referenced as TripFinder) is a full-stack Spring Boot application for planning trips, exploring destinations, and managing itineraries. The backend uses Spring Boot 3.3.0 with Java 17.

## Tech Stack

- **Framework**: Spring Boot 3.3.0 with Maven
- **Java Version**: 17
- **Database**: H2 (in-memory for development), MySQL (for production)
- **Security**: Spring Security with OAuth2 Resource Server
- **Build Tool**: Maven (with Maven Wrapper)

## Development Commands

### Build & Run
```bash
# Start the application (dev mode with auto-reload)
./mvnw spring-boot:run

# Build the project
./mvnw clean compile

# Package into JAR
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=TripFinderApplicationTests

# Run tests with coverage
./mvnw test jacoco:report
```

### Database
- H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:tripfinderdb`
- Username: `sa`
- Password: (empty)

### Useful Development Commands
```bash
# Clean and install dependencies
./mvnw clean install

# Generate sources and compile
./mvnw generate-sources compile

# Run in debug mode
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## Architecture Overview

The application follows a standard Spring Boot layered architecture:

### Package Structure
- `com.TripFinder` - Root package
- `controller/` - REST endpoints (AuthController, DestinationController, etc.)
- `service/` & `serviceImpl/` - Business logic layer
- `repository/` - Data access layer (JPA repositories)
- `entity/` - JPA entities (User, Destination, Itinerary, etc.)
- `dto/` - Data Transfer Objects for API requests/responses
- `response/` - Response wrapper classes
- `exception/` - Global exception handling

### Key Entities
- **User**: Implements UserDetails for Spring Security integration
- **Destination**: Travel destinations with location, city, description
- **Itinerary**: User trip plans with activities stored as JSON
- **Expense**: Trip expense tracking
- **Hotel**: Hotel information and bookings

### Security Configuration
- CORS enabled for localhost:5173 (frontend development)
- CSRF disabled for development convenience
- All endpoints currently permit all requests (development mode)
- BCrypt password encoding
- Email used as username for authentication

### Database Configuration
- H2 in-memory database for development
- JPA with Hibernate ORM
- Auto DDL update enabled
- SQL logging enabled for debugging

## API Structure

Base URL: `http://localhost:8080/api/v1/`

### Main Controllers
- `/auth` - Authentication endpoints (signup, login)
- `/users` - User management
- `/destinations` - Destination CRUD operations
- `/itineraries` - Trip itinerary management
- `/expenses` - Expense tracking
- `/hotels` - Hotel management
- `/flights` - Flight information

## Configuration Notes

### Frontend Integration
- CORS configured for React dev server on port 5173
- Additional CORS origin on port 3000 for flexibility

### Development Database
- H2 console accessible at `/h2-console`
- Database recreated on each application restart
- SQL queries logged to console for debugging

### Security Notes
- Currently in development mode with permissive security
- Password encoding with BCrypt is implemented
- OAuth2 Resource Server dependency included for future JWT implementation

## Important Patterns

### Service Layer
- Interface-based services with implementation classes
- Services handle business logic and validation
- Repository injection for data access

### Entity Relationships
- User entity implements Spring Security UserDetails
- Many-to-One relationships between entities and User
- JSON storage for complex data (activities, popular spots)

### Validation
- Bean validation with Jakarta annotations
- Global exception handler for consistent error responses
- Custom validation messages

### Development Features
- Spring Boot DevTools for hot reload
- Lombok for boilerplate reduction
- Comprehensive logging configuration
# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

**TripFinder** (also referenced as TripCraft) is a full-stack travel planning application that allows users to plan trips, explore destinations, manage itineraries, and track expenses. The application consists of a Spring Boot backend and a React frontend built with Vite.

## Tech Stack

### Backend (`/Backend`)
- **Framework**: Spring Boot 3.3.0 with Maven
- **Java Version**: 17
- **Database**: H2 (in-memory for development), MySQL (for production)
- **Security**: Spring Security with OAuth2 Resource Server, BCrypt password encoding
- **Build Tool**: Maven with Maven Wrapper
- **Key Dependencies**: JPA/Hibernate, Spring DevTools, Lombok, Validation

### Frontend (`/frontend`)
- **Framework**: React 19.1.1 with Vite 7.1.2
- **Styling**: Tailwind CSS 3.4.0
- **HTTP Client**: Axios 1.12.2
- **Routing**: React Router DOM 7.9.1
- **Icons**: Lucide React
- **Package Manager**: NPM

## Development Commands

### Full-Stack Development

#### Start Both Services (Development)
```powershell
# Terminal 1 - Start Backend
cd Backend
./mvnw spring-boot:run

# Terminal 2 - Start Frontend
cd frontend
npm run dev
```

### Backend Commands (`/Backend`)

#### Build & Run
```powershell
# Start the application (dev mode with auto-reload)
./mvnw spring-boot:run

# Build the project
./mvnw clean compile

# Package into JAR
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests

# Clean and install dependencies
./mvnw clean install
```

#### Testing
```powershell
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=TripFinderApplicationTests

# Run tests with coverage
./mvnw test jacoco:report
```

#### Debug Mode
```powershell
# Run in debug mode (port 5005)
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Frontend Commands (`/frontend`)

#### Development
```powershell
# Start development server (port 5173)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

#### Dependencies
```powershell
# Install dependencies
npm install

# Install new package
npm install <package-name>

# Install dev dependency
npm install --save-dev <package-name>
```

## Architecture Overview

### Full-Stack Architecture
The application follows a standard three-tier architecture:
- **Presentation Layer**: React frontend (port 5173)
- **API Layer**: Spring Boot REST API (port 8080)
- **Data Layer**: H2/MySQL database

### Backend Architecture (`com.TripFinder`)

#### Package Structure
- `controller/` - REST endpoints with CORS for localhost:5173
- `service/` - Business logic interfaces
- `serviceImpl/` - Service implementations
- `repository/` - JPA repositories for data access
- `entity/` - JPA entities with relationships
- `dto/` - Data Transfer Objects for API contracts
- `response/` - Response wrapper classes
- `exception/` - Global exception handling

#### Key Entities & Relationships
- **User**: Implements `UserDetails`, uses email as username
- **Destination**: Travel locations with weather integration
- **Itinerary**: User trip plans with JSON activity storage
- **Expense**: Trip expense tracking linked to users
- **Hotel**: Hotel information and bookings
- **Flight**: Integration with Amadeus API for flight search

#### Security Configuration
- CORS enabled for frontend development (localhost:5173, port 3000)
- CSRF disabled for development convenience
- BCrypt password encoding with 10 rounds
- JWT token generation (10-hour expiry)
- Currently permissive security for development

#### External API Integrations
- **Weather Data**: Open-Meteo API (geocoding + weather)
- **Flight Search**: Amadeus API (OAuth2 client credentials)

### Frontend Architecture (`/src`)

#### Key Technologies
- **Vite**: Fast build tool and dev server
- **Tailwind CSS**: Utility-first styling framework
- **React Router**: Client-side routing
- **Axios**: HTTP client for API communication

#### Development Configuration
- **Dev Server**: Hot module replacement on port 5173
- **ESLint**: Code linting with React hooks plugin
- **PostCSS**: CSS processing with Autoprefixer

## Database Access

### H2 Console (Development)
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:tripfinderdb`
- **Username**: `sa`
- **Password**: (empty)

### Database Configuration Notes
- H2 in-memory database recreated on each restart
- SQL queries logged to console for debugging
- JPA auto DDL update enabled
- MySQL connector included for production use

## API Structure

### Base URL
`http://localhost:8080/api/v1/`

### Main Endpoints
- `/auth` - User authentication (signup, login)
- `/users` - User management operations
- `/destinations` - Destination CRUD and weather data
- `/itineraries` - Trip itinerary management
- `/expenses` - Expense tracking
- `/hotels` - Hotel management
- `/flights` - Flight search (Amadeus integration)

### Frontend Routes
- Frontend accessible at: http://localhost:5173
- All routes handle React Router's client-side routing

## Environment Configuration

### Backend Environment Variables
Create `.env` file in `/Backend` (if needed):
```
OPENWEATHER_API_KEY=your_api_key_here
AMADEUS_API_KEY=your_amadeus_key
AMADEUS_API_SECRET=your_amadeus_secret
```

### Development Ports
- **Backend**: 8080
- **Frontend**: 5173 (Vite default)
- **H2 Console**: 8080/h2-console
- **Debug Port**: 5005 (when enabled)

## Key Development Patterns

### Backend Patterns
- **Service Layer Pattern**: Interface-based services with implementations
- **Repository Pattern**: JPA repositories with Spring Data
- **DTO Pattern**: Request/Response objects separate from entities
- **Global Exception Handling**: Centralized error responses
- **JWT Authentication**: Stateless authentication with Spring Security

### Frontend Patterns
- **Component-Based Architecture**: Reusable React components
- **API Integration**: Centralized Axios configuration
- **Responsive Design**: Mobile-first with Tailwind CSS
- **Modern React**: Functional components with hooks

### Security Considerations
- **Password Security**: BCrypt hashing with salt
- **CORS Configuration**: Specific origins allowed
- **Input Validation**: Jakarta Bean Validation
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries

### Code Quality Tools
- **Backend**: Spring Boot DevTools, Lombok annotations
- **Frontend**: ESLint with React rules, Vite HMR
- **Shared**: Git version control, VS Code configuration
# TripFinder - Travel Planner Application

A full-stack web application for planning trips, managing itineraries, tracking expenses, and exploring destinations.

## Features

- User authentication and registration
- Itinerary planning with drag-and-drop activities
- Expense tracking
- Hotel listings
- Destination exploration with weather information

## Setup Instructions

### Backend (Spring Boot)

1. Navigate to the `Backend` directory.
2. Ensure you have Java 17+ and Maven installed.
3. Run `mvn spring-boot:run` to start the backend server on port 8080.

### Frontend (React + Vite)

1. Navigate to the `frontend` directory.
2. Install dependencies: `npm install`
3. Start the development server: `npm run dev`
4. The frontend will run on port 5173.

## API Key Configuration for Weather Data

To integrate real-time weather data into the destination cards, follow these steps:

1. **Sign up for an API key** from [OpenWeatherMap](https://openweathermap.org/api):
   - Create a free account.
   - Generate an API key from your dashboard.

2. **Add the API key to your environment**:
   - In the `Backend` directory, create a `.env` file (or use application.properties).
   - Add: `OPENWEATHER_API_KEY=your_api_key_here`

3. **Modify the Destination entity** (if needed):
   - Ensure the `Destination` entity has a `weather` field to store temperature data.

4. **Update the backend controller**:
   - In `DestinationController.java`, add a method to fetch weather data using the API key.
   - Example endpoint: `GET /destinations/{id}/weather`

5. **Integrate in frontend**:
   - In `ExploreDestination.jsx`, fetch weather data for each destination and display it in the badge.

Example API call:
```
https://api.openweathermap.org/data/2.5/weather?q={city}&appid={API_KEY}&units=metric
```

This will provide temperature in Celsius. Parse the response and update the destination's weather field.

## Database

The application uses H2 in-memory database for development. For production, configure a persistent database like MySQL or PostgreSQL in `application.properties`.

## Technologies Used

- Backend: Spring Boot, JPA, Hibernate, BCrypt
- Frontend: React, Vite, Bootstrap
- Database: H2 (development), configurable for production

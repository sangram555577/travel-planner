# TripFinder - Travel Planner Application

A comprehensive full-stack travel planning application with Amadeus API integration for flight and hotel search, drag-and-drop itinerary management, and expense tracking.

## 🌟 Features

### Core Functionality
- **User Authentication**: Secure registration and login system
- **Flight Search**: Powered by Amadeus API with real-time flight data
- **Hotel Search**: Comprehensive hotel search with multiple room options
- **Smart Itinerary Planning**: Drag-and-drop interface with persistent reordering
- **Multi-Item Support**: Add flights, hotels, and activities to your trip
- **Expense Tracking**: Monitor your travel budget
- **Destination Exploration**: Discover new places with weather information

### Advanced Features
- **Pagination & Filtering**: Search results with advanced filters
- **Caching & Rate Limiting**: Optimized API performance
- **Fallback Data**: Graceful handling of API failures
- **Responsive Design**: Works on all device sizes
- **Real-time Updates**: Optimistic UI updates with error rollback
- **Admin Dashboard**: User and booking management with role-based access control

## 🚀 Quick Start

### Prerequisites
- Java 17+ and Maven 3.6+
- Node.js 16+ and npm/yarn
- Amadeus API credentials (free tier available)

### 1. Clone and Setup Environment

```bash
git clone <repository-url>
cd TripFinder
cp .env.template .env
```

### 2. Configure API Keys

Edit `.env` file with your credentials:

```env
# Amadeus API (Required for flight/hotel search)
AMADEUS_CLIENT_ID=your_amadeus_api_key_here
AMADEUS_CLIENT_SECRET=your_amadeus_api_secret_here

# Frontend Configuration
VITE_API_BASE_URL=http://localhost:8080/api/v1

# Optional: Weather API
OPENWEATHER_API_KEY=your_openweather_api_key_here
```

### 3. Start Backend (Spring Boot)

```bash
cd Backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Start Frontend (React + Vite)

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:5173`

## 🔑 API Key Setup

### Amadeus API (Flight & Hotel Search)

1. **Create Account**: Visit [Amadeus for Developers](https://developers.amadeus.com/)
2. **Create App**: Go to "My Apps" → "Create New App"
3. **Get Credentials**: Copy your API Key and API Secret
4. **Test Endpoints**: Use the test environment for development

**Free Tier Limits:**
- 2,000 API calls per month
- Test data only (not bookable)
- Perfect for development and demos

### OpenWeatherMap API (Optional)

1. **Sign up**: [OpenWeatherMap](https://openweathermap.org/api)
2. **Generate API key**: Available in your dashboard
3. **Free tier**: 1,000 calls/day, 60 calls/minute

## 🏗️ Architecture

### Backend Architecture
```
src/main/java/com/TripFinder/
├── controller/          # REST API endpoints
│   ├── FlightController.java
│   ├── HotelController.java
│   ├── ItineraryController.java
│   └── AdminController.java
├── service/            # Business logic
│   ├── FlightService.java
│   ├── HotelService.java
│   └── AdminService.java
├── entity/             # JPA entities
│   ├── User.java
│   ├── Itinerary.java
│   ├── ItineraryItem.java
│   └── Booking.java
├── dto/               # Data transfer objects
│   └── AdminUserDto.java
├── enums/             # Enumeration classes
│   └── Role.java
├── config/            # Configuration classes
└── repository/        # Data access layer
    ├── UserRepo.java
    ├── BookingRepo.java
    └── ItineraryRepo.java
```

### Frontend Architecture
```
src/
├── components/        # React components
│   ├── FlightSearch.jsx
│   ├── HotelSearch.jsx
│   ├── ItineraryPlanner.jsx
│   ├── AdminRoute.jsx
│   └── ProtectedRoute.jsx
├── pages/           # Page components
│   └── AdminPage.jsx
├── services/         # API integration
│   └── api.js       # Includes adminAPI
├── context/          # React context
│   └── AuthContext.jsx  # Role-based auth
└── assets/          # Static assets
```

## 🛠️ API Documentation

### Flight Endpoints

#### Search Flights
```http
POST /api/v1/flights/search?page=0&size=10&sortBy=price&sortOrder=asc
Content-Type: application/json

{
  "origin": "JFK",
  "destination": "LAX", 
  "departureDate": "2024-12-01",
  "adults": 1,
  "travelClass": "ECONOMY",
  "currency": "USD"
}
```

#### Get Flight Details
```http
GET /api/v1/flights/{offerId}
```

### Hotel Endpoints

#### Search Hotels
```http
POST /api/v1/hotels/search?page=0&size=20&sortBy=price&sortOrder=asc
Content-Type: application/json

{
  "cityCode": "NYC",
  "checkInDate": "2024-12-01",
  "checkOutDate": "2024-12-03",
  "adults": 2,
  "rooms": 1,
  "currency": "USD"
}
```

### Itinerary Endpoints

#### Create Itinerary
```http
POST /api/v1/itineraries
Content-Type: application/json
Authorization: Bearer {token}

{
  "tripName": "Paris Vacation",
  "startDate": "2024-12-01",
  "endDate": "2024-12-07",
  "userId": 1
}
```

#### Add Item to Itinerary
```http
POST /api/v1/itineraries/{id}/items/from-search
Content-Type: application/json
Authorization: Bearer {token}

{
  "type": "flight",
  "provider": "amadeus",
  "externalId": "flight-offer-123",
  "metadata": "{...flight data...}"
}
```

#### Reorder Items (Drag & Drop)
```http
POST /api/v1/itineraries/{id}/items/reorder
Content-Type: application/json
Authorization: Bearer {token}

[
  {"itemId": 1, "position": 1},
  {"itemId": 2, "position": 2}
]
```

### Admin Endpoints

#### Get All Users (Admin Only)
```http
GET /api/admin/users
Authorization: Bearer {admin_token}
```

#### Update User Role (Admin Only)
```http
PUT /api/admin/users/{userId}/role
Content-Type: application/json
Authorization: Bearer {admin_token}

{
  "role": "ADMIN" | "USER"
}
```

#### Get All Bookings (Admin Only)
```http
GET /api/admin/bookings
Authorization: Bearer {admin_token}
```

#### Delete Booking (Admin Only)
```http
DELETE /api/admin/bookings/{bookingId}
Authorization: Bearer {admin_token}
```

#### Get System Statistics (Admin Only)
```http
GET /api/admin/statistics
Authorization: Bearer {admin_token}
```

#### Get Admin Profile
```http
GET /api/admin/profile
Authorization: Bearer {admin_token}
```

## 👤 Admin Features

### Role-Based Access Control
- **User Role**: Default role for new registrations
- **Admin Role**: Full access to admin dashboard and management features

### Admin Dashboard Features
- **User Management**: View all users, change roles, view user statistics
- **Booking Management**: View all bookings, delete non-confirmed bookings
- **System Statistics**: Monitor app usage, user counts, booking stats
- **Real-time Data**: Auto-refresh capabilities with manual refresh option

### Creating an Admin User

**Option 1: Database Direct**
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@domain.com';
```

**Option 2: Application Properties (Development)**
```properties
app.admin.default-email=admin@example.com
app.admin.create-on-startup=true
```

### Admin Security
- All admin endpoints require `ROLE_ADMIN` authorization
- Admin role validation on both backend and frontend
- Protection against removing the last admin user
- Access denied pages for non-admin users

## 🧪 Testing

### Backend Tests
```bash
cd Backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

### Integration Testing
Use tools like Postman or curl to test API endpoints:

```bash
# Test flight search
curl -X POST "http://localhost:8080/api/v1/flights/search" \
  -H "Content-Type: application/json" \
  -d '{"origin":"JFK","destination":"LAX","departureDate":"2024-12-01","adults":1}'
```

## 🚀 Deployment

### Environment Variables

For production, set these environment variables:

```bash
AMADEUS_CLIENT_ID=prod_api_key
AMADEUS_CLIENT_SECRET=prod_api_secret
VITE_API_BASE_URL=https://your-domain.com/api/v1
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=your_production_db_url
```

### Docker Deployment (Optional)

```bash
# Build and run with Docker Compose
docker-compose up --build
```

## 🔧 Configuration

### Database Configuration

**Development (H2 In-Memory):**
```properties
spring.datasource.url=jdbc:h2:mem:tripfinderdb
spring.jpa.hibernate.ddl-auto=create-drop
```

**Production (MySQL/PostgreSQL):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tripfinderdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Caching Configuration

```properties
# Cache TTL (5 minutes)
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=300s

# Rate limiting
app.rate-limit.amadeus.requests-per-second=10
app.rate-limit.amadeus.burst-capacity=20
```

## 🐛 Troubleshooting

### Common Issues

1. **Amadeus API 401 Unauthorized**
   - Verify your API credentials in `.env`
   - Check if you're using test vs production endpoints

2. **CORS Issues**
   - Ensure frontend URL is in `@CrossOrigin` annotations
   - Check `FRONTEND_URL` configuration

3. **Database Connection Issues**
   - For H2: Check if port 8080 is available
   - For MySQL/PostgreSQL: Verify database credentials

4. **Flight/Hotel Search Returns Empty**
   - Check API key quotas
   - Verify date formats (YYYY-MM-DD)
   - Ensure airport/city codes are valid

### Debug Mode

Enable debug logging:

```properties
logging.level.com.TripFinder=DEBUG
logging.level.org.springframework.web=DEBUG
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Amadeus for Developers](https://developers.amadeus.com/) - Flight and hotel data
- [OpenWeatherMap](https://openweathermap.org/) - Weather information
- [Lucide React](https://lucide.dev/) - Beautiful icons
- [Tailwind CSS](https://tailwindcss.com/) - Styling framework

## 📞 Support

For support and questions:
- Create an issue in this repository
- Check the troubleshooting section above
- Review API documentation

---

**Happy Traveling! ✈️🏨🗺️**

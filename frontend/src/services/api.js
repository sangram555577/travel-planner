import axios from "axios";

// Base API configuration
const API = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 30000,
});

// Attach token (if logged in) to each request
API.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for error handling
API.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error);
    
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    
    return Promise.reject(error);
  }
);

// =============================================================================
// FLIGHT API ENDPOINTS
// =============================================================================

export const flightAPI = {
  // Search flights with pagination and filters
  searchFlights: async (searchParams, page = 0, size = 10, sortBy = 'price', sortOrder = 'asc') => {
    try {
      const response = await API.post(`/flights/search?page=${page}&size=${size}&sortBy=${sortBy}&sortOrder=${sortOrder}`, searchParams);
      return response.data;
    } catch (error) {
      throw new Error(`Flight search failed: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Legacy GET endpoint for backward compatibility
  getFlights: async (from, to, date, options = {}) => {
    const params = {
      from,
      to,
      date,
      adults: options.adults || 1,
      children: options.children || 0,
      travelClass: options.travelClass || 'ECONOMY',
      nonStop: options.nonStop || false,
      currency: options.currency || 'USD',
      page: options.page || 0,
      size: options.size || 10
    };
    
    try {
      const response = await API.get('/flights', { params });
      return response.data;
    } catch (error) {
      throw new Error(`Flight search failed: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Get flight details by offer ID
  getFlightDetails: async (offerId) => {
    try {
      const response = await API.get(`/flights/${offerId}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get flight details: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Get available destinations from origin
  getDestinations: async (origin) => {
    try {
      const response = await API.get('/flights/destinations', { params: { origin } });
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get destinations: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Get popular flight routes
  getPopularRoutes: async () => {
    try {
      const response = await API.get('/flights/popular-routes');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get popular routes: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Clear flight cache (admin)
  clearCache: async () => {
    try {
      const response = await API.post('/flights/cache/clear');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to clear cache: ${error.response?.data?.message || error.message}`);
    }
  }
};

// =============================================================================
// HOTEL API ENDPOINTS
// =============================================================================

export const hotelAPI = {
  // Search hotels with pagination and filters
  searchHotels: async (searchParams, page = 0, size = 20, sortBy = 'price', sortOrder = 'asc') => {
    try {
      const response = await API.post(`/hotels/search?page=${page}&size=${size}&sortBy=${sortBy}&sortOrder=${sortOrder}`, searchParams);
      return response.data;
    } catch (error) {
      throw new Error(`Hotel search failed: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // GET endpoint for hotel search (backward compatibility)
  getHotels: async (cityCode, checkIn, checkOut, options = {}) => {
    const params = {
      cityCode,
      checkIn,
      checkOut,
      adults: options.adults || 1,
      rooms: options.rooms || 1,
      currency: options.currency || 'USD',
      page: options.page || 0,
      size: options.size || 20,
      sortBy: options.sortBy || 'price',
      sortOrder: options.sortOrder || 'asc'
    };
    
    try {
      const response = await API.get('/hotels/search', { params });
      return response.data;
    } catch (error) {
      throw new Error(`Hotel search failed: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Get hotel details by hotel ID and offer ID
  getHotelDetails: async (hotelId, offerId) => {
    try {
      const response = await API.get(`/hotels/${hotelId}/offers/${offerId}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get hotel details: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Get hotels by location (simplified)
  getHotelsByLocation: async (cityCode, checkIn, checkOut, adults = 1) => {
    try {
      const response = await API.get('/hotels/location', {
        params: { cityCode, checkIn, checkOut, adults }
      });
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get hotels: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Get popular hotel destinations
  getPopularDestinations: async () => {
    try {
      const response = await API.get('/hotels/popular-destinations');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get popular destinations: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Get all hotels from database (legacy)
  getAllHotels: async () => {
    try {
      const response = await API.get('/hotels/all');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get all hotels: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Clear hotel cache (admin)
  clearCache: async () => {
    try {
      const response = await API.post('/hotels/cache/clear');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to clear cache: ${error.response?.data?.message || error.message}`);
    }
  }
};

// =============================================================================
// ITINERARY API ENDPOINTS
// =============================================================================

export const itineraryAPI = {
  // Basic itinerary operations
  createItinerary: async (itineraryData) => {
    try {
      const response = await API.post('/itineraries', itineraryData);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to create itinerary: ${error.response?.data?.message || error.message}`);
    }
  },
  
  getItinerariesByUser: async (userId) => {
    try {
      const response = await API.get(`/itineraries/user/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get itineraries: ${error.response?.data?.message || error.message}`);
    }
  },
  
  getItinerary: async (id) => {
    try {
      const response = await API.get(`/itineraries/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get itinerary: ${error.response?.data?.message || error.message}`);
    }
  },
  
  updateItinerary: async (id, itineraryData) => {
    try {
      const response = await API.put(`/itineraries/${id}`, itineraryData);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to update itinerary: ${error.response?.data?.message || error.message}`);
    }
  },
  
  deleteItinerary: async (id) => {
    try {
      await API.delete(`/itineraries/${id}`);
      return true;
    } catch (error) {
      throw new Error(`Failed to delete itinerary: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Item management
  addItem: async (itineraryId, itemData) => {
    try {
      const response = await API.post(`/itineraries/${itineraryId}/items`, itemData);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to add item: ${error.response?.data?.message || error.message}`);
    }
  },
  
  addItemFromSearch: async (itineraryId, searchData) => {
    try {
      const response = await API.post(`/itineraries/${itineraryId}/items/from-search`, searchData);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to add item from search: ${error.response?.data?.message || error.message}`);
    }
  },
  
  getItems: async (itineraryId) => {
    try {
      const response = await API.get(`/itineraries/${itineraryId}/items`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get items: ${error.response?.data?.message || error.message}`);
    }
  },
  
  updateItem: async (itineraryId, itemId, itemData) => {
    try {
      const response = await API.put(`/itineraries/${itineraryId}/items/${itemId}`, itemData);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to update item: ${error.response?.data?.message || error.message}`);
    }
  },
  
  removeItem: async (itineraryId, itemId) => {
    try {
      await API.delete(`/itineraries/${itineraryId}/items/${itemId}`);
      return true;
    } catch (error) {
      throw new Error(`Failed to remove item: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Reordering and positioning
  reorderItems: async (itineraryId, itemOrders) => {
    try {
      const response = await API.post(`/itineraries/${itineraryId}/items/reorder`, itemOrders);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to reorder items: ${error.response?.data?.message || error.message}`);
    }
  },
  
  moveItem: async (itineraryId, itemId, newPosition) => {
    try {
      const response = await API.post(`/itineraries/${itineraryId}/items/${itemId}/move`, null, {
        params: { newPosition }
      });
      return response.data;
    } catch (error) {
      throw new Error(`Failed to move item: ${error.response?.data?.message || error.message}`);
    }
  },
  
  // Filtering and statistics
  getItemsByType: async (itineraryId, type) => {
    try {
      const response = await API.get(`/itineraries/${itineraryId}/items/type/${type}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get items by type: ${error.response?.data?.message || error.message}`);
    }
  },
  
  getStatistics: async (itineraryId) => {
    try {
      const response = await API.get(`/itineraries/${itineraryId}/stats`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get statistics: ${error.response?.data?.message || error.message}`);
    }
  }
};

// =============================================================================
// UTILITY FUNCTIONS
// =============================================================================

export const apiUtils = {
  // Format date for API calls
  formatDate: (date) => {
    if (date instanceof Date) {
      return date.toISOString().split('T')[0];
    }
    return date;
  },
  
  // Build pagination params
  buildPaginationParams: (page = 0, size = 10, sortBy = null, sortOrder = 'asc') => {
    const params = { page, size };
    if (sortBy) {
      params.sortBy = sortBy;
      params.sortOrder = sortOrder;
    }
    return params;
  },
  
  // Handle API errors consistently
  handleApiError: (error, defaultMessage = 'An error occurred') => {
    console.error('API Error:', error);
    
    if (error.response?.data?.message) {
      return error.response.data.message;
    }
    
    if (error.message) {
      return error.message;
    }
    
    return defaultMessage;
  },
  
  // Check if request should be retried
  shouldRetry: (error) => {
    return error.code === 'NETWORK_ERROR' || 
           error.response?.status >= 500 ||
           error.response?.status === 429; // Rate limited
  }
};

// =============================================================================
// ADMIN API ENDPOINTS
// =============================================================================

export const adminAPI = {
  // User management
  getAllUsers: async () => {
    try {
      const response = await API.get('/admin/users');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get users: ${error.response?.data?.error || error.message}`);
    }
  },
  
  updateUserRole: async (userId, role) => {
    try {
      const response = await API.put(`/admin/users/${userId}/role`, { role });
      return response.data;
    } catch (error) {
      throw new Error(`Failed to update user role: ${error.response?.data?.error || error.message}`);
    }
  },
  
  getUserStatistics: async (userId) => {
    try {
      const response = await API.get(`/admin/users/${userId}/statistics`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get user statistics: ${error.response?.data?.error || error.message}`);
    }
  },
  
  // Booking management
  getAllBookings: async () => {
    try {
      const response = await API.get('/admin/bookings');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get bookings: ${error.response?.data?.error || error.message}`);
    }
  },
  
  deleteBooking: async (bookingId) => {
    try {
      const response = await API.delete(`/admin/bookings/${bookingId}`);
      return response.data;
    } catch (error) {
      throw new Error(`Failed to delete booking: ${error.response?.data?.error || error.message}`);
    }
  },
  
  // System statistics and monitoring
  getSystemStatistics: async () => {
    try {
      const response = await API.get('/admin/statistics');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get system statistics: ${error.response?.data?.error || error.message}`);
    }
  },
  
  getAdminProfile: async () => {
    try {
      const response = await API.get('/admin/profile');
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get admin profile: ${error.response?.data?.error || error.message}`);
    }
  },
  
  // Cache management
  clearFlightCache: async () => {
    try {
      const response = await flightAPI.clearCache();
      return response;
    } catch (error) {
      throw new Error(`Failed to clear flight cache: ${error.message}`);
    }
  },
  
  clearHotelCache: async () => {
    try {
      const response = await hotelAPI.clearCache();
      return response;
    } catch (error) {
      throw new Error(`Failed to clear hotel cache: ${error.message}`);
    }
  }
};

export default API;

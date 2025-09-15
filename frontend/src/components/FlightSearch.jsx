import React, { useState, useEffect, useContext } from 'react';
import { flightAPI, itineraryAPI, apiUtils } from '../services/api';
import { AuthContext } from '../context/AuthContext';
import { TripContext } from '../context/TripContext';
import { Search, Filter, ChevronDown, ChevronUp, Plane, Clock, DollarSign, Plus, Calendar, Users, ArrowRight, Loader } from 'lucide-react';

const FlightSearch = ({ onAddToItinerary, selectedItinerary }) => {
  const { user } = useContext(AuthContext);
  const { tripDetails } = useContext(TripContext);

  // Search form state
  const [searchForm, setSearchForm] = useState({
    origin: '',
    destination: '',
    departureDate: '',
    returnDate: '',
    adults: 1,
    children: 0,
    infants: 0,
    travelClass: 'ECONOMY',
    nonStop: false,
    currency: 'USD'
  });

  // Search results and pagination
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0,
    sortBy: 'price',
    sortOrder: 'asc'
  });

  // Filters
  const [filters, setFilters] = useState({
    maxPrice: '',
    maxDuration: '',
    airline: '',
    showFilters: false
  });

  // UI state
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [expandedFlight, setExpandedFlight] = useState(null);

  // Popular routes for suggestions
  const [popularRoutes, setPopularRoutes] = useState([]);

  useEffect(() => {
    loadPopularRoutes();
    
    // Pre-fill from trip context if available
    if (tripDetails.city) {
      setSearchForm(prev => ({
        ...prev,
        destination: tripDetails.city,
        departureDate: tripDetails.startDate || '',
        returnDate: tripDetails.endDate || ''
      }));
    }
  }, [tripDetails]);

  const loadPopularRoutes = async () => {
    try {
      const routes = await flightAPI.getPopularRoutes();
      setPopularRoutes(routes);
    } catch (error) {
      console.error('Failed to load popular routes:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setSearchForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const searchFlights = async (page = 0, sortBy = null, sortOrder = null) => {
    if (!searchForm.origin || !searchForm.destination || !searchForm.departureDate) {
      setError('Please fill in origin, destination, and departure date');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const searchParams = {
        origin: searchForm.origin.toUpperCase(),
        destination: searchForm.destination.toUpperCase(),
        departureDate: searchForm.departureDate,
        returnDate: searchForm.returnDate || null,
        adults: searchForm.adults,
        children: searchForm.children,
        infants: searchForm.infants,
        travelClass: searchForm.travelClass,
        nonStop: searchForm.nonStop,
        currency: searchForm.currency,
        maxPrice: filters.maxPrice ? parseFloat(filters.maxPrice) : null,
        maxDuration: filters.maxDuration ? parseInt(filters.maxDuration) : null,
        airline: filters.airline || null
      };

      const currentSortBy = sortBy || pagination.sortBy;
      const currentSortOrder = sortOrder || pagination.sortOrder;

      const result = await flightAPI.searchFlights(
        searchParams, 
        page, 
        pagination.size, 
        currentSortBy, 
        currentSortOrder
      );

      setFlights(result.content || []);
      setPagination(prev => ({
        ...prev,
        page: result.number || page,
        totalPages: result.totalPages || 0,
        totalElements: result.totalElements || 0,
        sortBy: currentSortBy,
        sortOrder: currentSortOrder
      }));
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to search flights'));
    } finally {
      setLoading(false);
    }
  };

  const handleSortChange = (sortBy) => {
    const newSortOrder = pagination.sortBy === sortBy && pagination.sortOrder === 'asc' ? 'desc' : 'asc';
    searchFlights(0, sortBy, newSortOrder);
  };

  const handlePageChange = (newPage) => {
    searchFlights(newPage);
  };

  const addFlightToItinerary = async (flight) => {
    if (!selectedItinerary) {
      setError('Please select an itinerary first');
      return;
    }

    try {
      const flightData = {
        type: 'flight',
        provider: 'amadeus',
        externalId: flight.id,
        metadata: JSON.stringify(flight)
      };

      await itineraryAPI.addItemFromSearch(selectedItinerary.id, flightData);
      setSuccess('Flight added to itinerary successfully!');
      
      if (onAddToItinerary) {
        onAddToItinerary(flight, 'flight');
      }

      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to add flight to itinerary'));
    }
  };

  const formatDuration = (duration) => {
    if (!duration) return 'N/A';
    // Convert PT4H30M format to readable format
    const match = duration.match(/PT(\d+)H(\d+)M/);
    if (match) {
      const hours = match[1];
      const minutes = match[2];
      return `${hours}h ${minutes}m`;
    }
    return duration;
  };

  const formatDateTime = (dateTime) => {
    if (!dateTime) return 'N/A';
    const date = new Date(dateTime);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const toggleFlightDetails = (flightId) => {
    setExpandedFlight(expandedFlight === flightId ? null : flightId);
  };

  return (
    <div className="max-w-7xl mx-auto p-6">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">Search Flights</h1>
        <p className="text-gray-600">Find and book the best flights for your trip</p>
      </div>

      {/* Search Form */}
      <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">From</label>
            <input
              type="text"
              name="origin"
              value={searchForm.origin}
              onChange={handleInputChange}
              placeholder="Origin airport code"
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">To</label>
            <input
              type="text"
              name="destination"
              value={searchForm.destination}
              onChange={handleInputChange}
              placeholder="Destination airport code"
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Departure</label>
            <input
              type="date"
              name="departureDate"
              value={searchForm.departureDate}
              onChange={handleInputChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Return (optional)</label>
            <input
              type="date"
              name="returnDate"
              value={searchForm.returnDate}
              onChange={handleInputChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Adults</label>
            <input
              type="number"
              name="adults"
              value={searchForm.adults}
              onChange={handleInputChange}
              min="1"
              max="9"
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Children</label>
            <input
              type="number"
              name="children"
              value={searchForm.children}
              onChange={handleInputChange}
              min="0"
              max="9"
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Class</label>
            <select
              name="travelClass"
              value={searchForm.travelClass}
              onChange={handleInputChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ECONOMY">Economy</option>
              <option value="PREMIUM_ECONOMY">Premium Economy</option>
              <option value="BUSINESS">Business</option>
              <option value="FIRST">First</option>
            </select>
          </div>
          
          <div className="flex items-center mt-6">
            <label className="flex items-center">
              <input
                type="checkbox"
                name="nonStop"
                checked={searchForm.nonStop}
                onChange={handleInputChange}
                className="mr-2"
              />
              <span className="text-sm text-gray-700">Non-stop only</span>
            </label>
          </div>
        </div>

        {/* Filter Toggle */}
        <div className="flex items-center justify-between mb-4">
          <button
            onClick={() => setFilters(prev => ({ ...prev, showFilters: !prev.showFilters }))}
            className="flex items-center text-blue-600 hover:text-blue-800"
          >
            <Filter className="w-4 h-4 mr-1" />
            Filters
            {filters.showFilters ? <ChevronUp className="w-4 h-4 ml-1" /> : <ChevronDown className="w-4 h-4 ml-1" />}
          </button>
        </div>

        {/* Filters */}
        {filters.showFilters && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4 p-4 bg-gray-50 rounded-lg">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Max Price ({searchForm.currency})</label>
              <input
                type="number"
                name="maxPrice"
                value={filters.maxPrice}
                onChange={handleFilterChange}
                placeholder="e.g., 500"
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Max Duration (minutes)</label>
              <input
                type="number"
                name="maxDuration"
                value={filters.maxDuration}
                onChange={handleFilterChange}
                placeholder="e.g., 480"
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Airline</label>
              <input
                type="text"
                name="airline"
                value={filters.airline}
                onChange={handleFilterChange}
                placeholder="e.g., AA, DL, UA"
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
        )}

        {/* Search Button */}
        <div className="flex flex-col sm:flex-row gap-4">
          <button
            onClick={() => searchFlights(0)}
            disabled={loading}
            className="flex items-center justify-center bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex-1"
          >
            {loading ? <Loader className="w-4 h-4 mr-2 animate-spin" /> : <Search className="w-4 h-4 mr-2" />}
            {loading ? 'Searching...' : 'Search Flights'}
          </button>
        </div>
      </div>

      {/* Error/Success Messages */}
      {error && (
        <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {success && (
        <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
          {success}
        </div>
      )}

      {/* Results */}
      {flights.length > 0 && (
        <div className="bg-white rounded-lg shadow-lg">
          {/* Results Header */}
          <div className="p-6 border-b border-gray-200">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
              <div>
                <h3 className="text-xl font-semibold text-gray-800">
                  {pagination.totalElements} flights found
                </h3>
                <p className="text-gray-600">
                  Showing page {pagination.page + 1} of {pagination.totalPages}
                </p>
              </div>
              
              {/* Sort Options */}
              <div className="flex gap-2">
                <button
                  onClick={() => handleSortChange('price')}
                  className={`px-4 py-2 rounded-lg text-sm ${
                    pagination.sortBy === 'price'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Price {pagination.sortBy === 'price' && (pagination.sortOrder === 'asc' ? '↑' : '↓')}
                </button>
                <button
                  onClick={() => handleSortChange('duration')}
                  className={`px-4 py-2 rounded-lg text-sm ${
                    pagination.sortBy === 'duration'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Duration {pagination.sortBy === 'duration' && (pagination.sortOrder === 'asc' ? '↑' : '↓')}
                </button>
              </div>
            </div>
          </div>

          {/* Flight Results */}
          <div className="divide-y divide-gray-200">
            {flights.map((flight) => (
              <div key={flight.id} className="p-6">
                <div className="flex flex-col lg:flex-row justify-between items-start gap-4">
                  {/* Flight Info */}
                  <div className="flex-1">
                    {flight.itineraries && flight.itineraries.map((itinerary, idx) => (
                      <div key={idx} className="mb-4">
                        <div className="flex items-center gap-4 mb-2">
                          <div className="flex items-center">
                            <Plane className="w-4 h-4 text-blue-600 mr-2" />
                            <span className="font-medium">
                              {itinerary.segments?.[0]?.departure?.iataCode} 
                              <ArrowRight className="w-4 h-4 mx-2 inline" />
                              {itinerary.segments?.[itinerary.segments.length - 1]?.arrival?.iataCode}
                            </span>
                          </div>
                          
                          <div className="flex items-center text-gray-600">
                            <Clock className="w-4 h-4 mr-1" />
                            {formatDuration(itinerary.duration)}
                          </div>
                          
                          {itinerary.segments && itinerary.segments.length > 1 && (
                            <span className="text-sm text-gray-500">
                              {itinerary.segments.length - 1} stop{itinerary.segments.length > 2 ? 's' : ''}
                            </span>
                          )}
                        </div>
                        
                        <div className="text-sm text-gray-600">
                          {formatDateTime(itinerary.segments?.[0]?.departure?.at)} - {formatDateTime(itinerary.segments?.[itinerary.segments.length - 1]?.arrival?.at)}
                        </div>
                        
                        {itinerary.segments && (
                          <div className="text-sm text-gray-500 mt-1">
                            {itinerary.segments.map(segment => segment.carrierCode).join(', ')}
                          </div>
                        )}
                      </div>
                    ))}
                  </div>

                  {/* Price and Actions */}
                  <div className="flex flex-col items-end gap-2">
                    <div className="text-right">
                      <div className="text-2xl font-bold text-gray-800">
                        {flight.currency} {flight.price?.toFixed(2)}
                      </div>
                      {flight.numberOfBookableSeats && (
                        <div className="text-sm text-gray-500">
                          {flight.numberOfBookableSeats} seats left
                        </div>
                      )}
                    </div>
                    
                    <div className="flex gap-2">
                      <button
                        onClick={() => toggleFlightDetails(flight.id)}
                        className="px-4 py-2 text-blue-600 border border-blue-600 rounded-lg hover:bg-blue-50"
                      >
                        {expandedFlight === flight.id ? 'Hide Details' : 'View Details'}
                      </button>
                      
                      {selectedItinerary && (
                        <button
                          onClick={() => addFlightToItinerary(flight)}
                          className="flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                        >
                          <Plus className="w-4 h-4 mr-1" />
                          Add to Trip
                        </button>
                      )}
                    </div>
                  </div>
                </div>

                {/* Expanded Details */}
                {expandedFlight === flight.id && flight.itineraries && (
                  <div className="mt-4 p-4 bg-gray-50 rounded-lg">
                    <h4 className="font-semibold mb-3">Flight Details</h4>
                    {flight.itineraries.map((itinerary, itIdx) => (
                      <div key={itIdx} className="mb-4">
                        <h5 className="font-medium mb-2">
                          {flight.itineraries.length > 1 ? `Journey ${itIdx + 1}` : 'Outbound'}
                        </h5>
                        {itinerary.segments && itinerary.segments.map((segment, segIdx) => (
                          <div key={segIdx} className="mb-3 p-3 bg-white rounded border">
                            <div className="flex justify-between items-start">
                              <div>
                                <div className="font-medium">
                                  {segment.carrierCode} {segment.number}
                                </div>
                                <div className="text-sm text-gray-600">
                                  {segment.aircraft?.code} • {formatDuration(segment.duration)}
                                </div>
                              </div>
                              <div className="text-right text-sm">
                                <div>{formatDateTime(segment.departure?.at)}</div>
                                <div className="text-gray-600">{segment.departure?.iataCode}</div>
                                {segment.departure?.terminal && (
                                  <div className="text-xs text-gray-500">Terminal {segment.departure.terminal}</div>
                                )}
                              </div>
                              <ArrowRight className="w-4 h-4 text-gray-400 mx-4 mt-2" />
                              <div className="text-right text-sm">
                                <div>{formatDateTime(segment.arrival?.at)}</div>
                                <div className="text-gray-600">{segment.arrival?.iataCode}</div>
                                {segment.arrival?.terminal && (
                                  <div className="text-xs text-gray-500">Terminal {segment.arrival.terminal}</div>
                                )}
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>

          {/* Pagination */}
          {pagination.totalPages > 1 && (
            <div className="p-6 border-t border-gray-200">
              <div className="flex justify-center gap-2">
                <button
                  onClick={() => handlePageChange(pagination.page - 1)}
                  disabled={pagination.page === 0}
                  className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  Previous
                </button>
                
                {Array.from({ length: Math.min(5, pagination.totalPages) }, (_, i) => {
                  const pageNum = Math.max(0, Math.min(
                    pagination.totalPages - 5,
                    pagination.page - 2
                  )) + i;
                  
                  return (
                    <button
                      key={pageNum}
                      onClick={() => handlePageChange(pageNum)}
                      className={`px-4 py-2 border rounded-lg ${
                        pagination.page === pageNum
                          ? 'bg-blue-600 text-white border-blue-600'
                          : 'border-gray-300 hover:bg-gray-50'
                      }`}
                    >
                      {pageNum + 1}
                    </button>
                  );
                })}
                
                <button
                  onClick={() => handlePageChange(pagination.page + 1)}
                  disabled={pagination.page >= pagination.totalPages - 1}
                  className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Popular Routes Suggestions */}
      {flights.length === 0 && !loading && popularRoutes.length > 0 && (
        <div className="bg-white rounded-lg shadow-lg p-6">
          <h3 className="text-xl font-semibold text-gray-800 mb-4">Popular Routes</h3>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
            {popularRoutes.slice(0, 8).map((route, index) => (
              <button
                key={index}
                onClick={() => {
                  const [origin, destination] = route.split('-');
                  setSearchForm(prev => ({
                    ...prev,
                    origin,
                    destination
                  }));
                }}
                className="p-3 border border-gray-300 rounded-lg hover:bg-blue-50 hover:border-blue-300 text-left"
              >
                <div className="font-medium text-sm">{route}</div>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default FlightSearch;
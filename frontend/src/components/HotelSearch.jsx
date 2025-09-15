import React, { useState, useEffect, useContext } from 'react';
import { hotelAPI, itineraryAPI, apiUtils } from '../services/api';
import { AuthContext } from '../context/AuthContext';
import { TripContext } from '../context/TripContext';
import { Search, Filter, MapPin, Star, Wifi, Car, Coffee, Plus, Calendar, Users, Loader } from 'lucide-react';

const HotelSearch = ({ onAddToItinerary, selectedItinerary }) => {
  const { user } = useContext(AuthContext);
  const { tripDetails } = useContext(TripContext);

  const [searchForm, setSearchForm] = useState({
    cityCode: '',
    checkInDate: '',
    checkOutDate: '',
    adults: 1,
    rooms: 1,
    currency: 'USD'
  });

  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    totalPages: 0,
    totalElements: 0,
    sortBy: 'price',
    sortOrder: 'asc'
  });

  const [filters, setFilters] = useState({
    priceMin: '',
    priceMax: '',
    ratings: [],
    amenities: [],
    showFilters: false
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [popularDestinations, setPopularDestinations] = useState([]);

  useEffect(() => {
    loadPopularDestinations();
    
    if (tripDetails.city) {
      setSearchForm(prev => ({
        ...prev,
        cityCode: tripDetails.city,
        checkInDate: tripDetails.startDate || '',
        checkOutDate: tripDetails.endDate || ''
      }));
    }
  }, [tripDetails]);

  const loadPopularDestinations = async () => {
    try {
      const destinations = await hotelAPI.getPopularDestinations();
      setPopularDestinations(destinations);
    } catch (error) {
      console.error('Failed to load popular destinations:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setSearchForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleFilterChange = (name, value) => {
    if (name === 'ratings') {
      setFilters(prev => ({
        ...prev,
        ratings: prev.ratings.includes(value)
          ? prev.ratings.filter(r => r !== value)
          : [...prev.ratings, value]
      }));
    } else if (name === 'amenities') {
      setFilters(prev => ({
        ...prev,
        amenities: prev.amenities.includes(value)
          ? prev.amenities.filter(a => a !== value)
          : [...prev.amenities, value]
      }));
    } else {
      setFilters(prev => ({ ...prev, [name]: value }));
    }
  };

  const searchHotels = async (page = 0, sortBy = null, sortOrder = null) => {
    if (!searchForm.cityCode || !searchForm.checkInDate || !searchForm.checkOutDate) {
      setError('Please fill in city, check-in, and check-out dates');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const searchParams = {
        cityCode: searchForm.cityCode.toUpperCase(),
        checkInDate: searchForm.checkInDate,
        checkOutDate: searchForm.checkOutDate,
        adults: searchForm.adults,
        rooms: searchForm.rooms,
        currency: searchForm.currency,
        priceMin: filters.priceMin ? parseFloat(filters.priceMin) : null,
        priceMax: filters.priceMax ? parseFloat(filters.priceMax) : null,
        ratings: filters.ratings.length > 0 ? filters.ratings : null,
        amenities: filters.amenities.length > 0 ? filters.amenities : null
      };

      const currentSortBy = sortBy || pagination.sortBy;
      const currentSortOrder = sortOrder || pagination.sortOrder;

      const result = await hotelAPI.searchHotels(
        searchParams,
        page,
        pagination.size,
        currentSortBy,
        currentSortOrder
      );

      setHotels(result.content || []);
      setPagination(prev => ({
        ...prev,
        page: result.number || page,
        totalPages: result.totalPages || 0,
        totalElements: result.totalElements || 0,
        sortBy: currentSortBy,
        sortOrder: currentSortOrder
      }));
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to search hotels'));
    } finally {
      setLoading(false);
    }
  };

  const addHotelToItinerary = async (hotel, offer) => {
    if (!selectedItinerary) {
      setError('Please select an itinerary first');
      return;
    }

    try {
      const hotelData = {
        type: 'hotel',
        provider: 'amadeus',
        externalId: offer.id,
        metadata: JSON.stringify({ hotel, offer })
      };

      await itineraryAPI.addItemFromSearch(selectedItinerary.id, hotelData);
      setSuccess('Hotel added to itinerary successfully!');
      
      if (onAddToItinerary) {
        onAddToItinerary({ hotel, offer }, 'hotel');
      }

      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      setError(apiUtils.handleApiError(error, 'Failed to add hotel to itinerary'));
    }
  };

  const renderStars = (rating) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star
        key={i}
        className={`w-4 h-4 ${i < rating ? 'text-yellow-400 fill-current' : 'text-gray-300'}`}
      />
    ));
  };

  const getAmenityIcon = (amenity) => {
    const icons = {
      'WIFI': Wifi,
      'PARKING': Car,
      'RESTAURANT': Coffee
    };
    const Icon = icons[amenity] || MapPin;
    return <Icon className="w-4 h-4" />;
  };

  return (
    <div className="max-w-7xl mx-auto p-6">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">Search Hotels</h1>
        <p className="text-gray-600">Find the perfect accommodation for your trip</p>
      </div>

      {/* Search Form */}
      <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">City</label>
            <input
              type="text"
              name="cityCode"
              value={searchForm.cityCode}
              onChange={handleInputChange}
              placeholder="City code (e.g., NYC, LON)"
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Check-in</label>
            <input
              type="date"
              name="checkInDate"
              value={searchForm.checkInDate}
              onChange={handleInputChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Check-out</label>
            <input
              type="date"
              name="checkOutDate"
              value={searchForm.checkOutDate}
              onChange={handleInputChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Guests</label>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="number"
                name="adults"
                value={searchForm.adults}
                onChange={handleInputChange}
                min="1"
                max="9"
                placeholder="Adults"
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              <input
                type="number"
                name="rooms"
                value={searchForm.rooms}
                onChange={handleInputChange}
                min="1"
                max="9"
                placeholder="Rooms"
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
        </div>

        {/* Filters */}
        <div className="mb-4">
          <button
            onClick={() => handleFilterChange('showFilters', !filters.showFilters)}
            className="flex items-center text-blue-600 hover:text-blue-800 mb-4"
          >
            <Filter className="w-4 h-4 mr-1" />
            Filters
          </button>

          {filters.showFilters && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 p-4 bg-gray-50 rounded-lg">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Price Range</label>
                <div className="grid grid-cols-2 gap-2">
                  <input
                    type="number"
                    placeholder="Min"
                    value={filters.priceMin}
                    onChange={(e) => handleFilterChange('priceMin', e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                  />
                  <input
                    type="number"
                    placeholder="Max"
                    value={filters.priceMax}
                    onChange={(e) => handleFilterChange('priceMax', e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Star Rating</label>
                <div className="flex gap-2">
                  {[3, 4, 5].map(rating => (
                    <button
                      key={rating}
                      onClick={() => handleFilterChange('ratings', rating)}
                      className={`px-3 py-1 rounded text-sm ${
                        filters.ratings.includes(rating)
                          ? 'bg-blue-600 text-white'
                          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                      }`}
                    >
                      {rating}★
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Amenities</label>
                <div className="space-y-1">
                  {['WIFI', 'PARKING', 'RESTAURANT'].map(amenity => (
                    <label key={amenity} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={filters.amenities.includes(amenity)}
                        onChange={() => handleFilterChange('amenities', amenity)}
                        className="mr-2"
                      />
                      <span className="text-sm">{amenity.toLowerCase()}</span>
                    </label>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        <button
          onClick={() => searchHotels(0)}
          disabled={loading}
          className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:opacity-50 flex items-center justify-center"
        >
          {loading ? <Loader className="w-4 h-4 mr-2 animate-spin" /> : <Search className="w-4 h-4 mr-2" />}
          {loading ? 'Searching...' : 'Search Hotels'}
        </button>
      </div>

      {/* Messages */}
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
      {hotels.length > 0 && (
        <div className="bg-white rounded-lg shadow-lg">
          <div className="p-6 border-b border-gray-200">
            <h3 className="text-xl font-semibold text-gray-800">
              {pagination.totalElements} hotels found
            </h3>
          </div>

          <div className="divide-y divide-gray-200">
            {hotels.map((hotel) => (
              <div key={hotel.hotelId} className="p-6">
                <div className="flex flex-col lg:flex-row gap-6">
                  <div className="flex-1">
                    <div className="flex items-start justify-between mb-2">
                      <h3 className="text-xl font-semibold text-gray-800">{hotel.name}</h3>
                      <div className="flex items-center">
                        {renderStars(hotel.rating)}
                        <span className="ml-2 text-sm text-gray-600">{hotel.rating} stars</span>
                      </div>
                    </div>

                    {hotel.address && (
                      <div className="flex items-center text-gray-600 mb-2">
                        <MapPin className="w-4 h-4 mr-1" />
                        <span className="text-sm">
                          {hotel.address.lines?.join(', ')}, {hotel.address.cityName}
                        </span>
                      </div>
                    )}

                    {hotel.description && (
                      <p className="text-gray-600 text-sm mb-3">{hotel.description}</p>
                    )}

                    {hotel.amenities && hotel.amenities.length > 0 && (
                      <div className="flex flex-wrap gap-2 mb-3">
                        {hotel.amenities.slice(0, 5).map((amenity, idx) => (
                          <div key={idx} className="flex items-center bg-gray-100 px-2 py-1 rounded text-xs">
                            {getAmenityIcon(amenity)}
                            <span className="ml-1">{amenity.toLowerCase()}</span>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>

                  <div className="lg:w-64">
                    {hotel.offers && hotel.offers.length > 0 && (
                      <div className="space-y-3">
                        {hotel.offers.slice(0, 2).map((offer, idx) => (
                          <div key={idx} className="border rounded-lg p-4">
                            <div className="flex justify-between items-start mb-2">
                              <div>
                                <div className="font-semibold text-lg">
                                  {offer.price?.currency} {offer.price?.total?.toFixed(2)}
                                </div>
                                <div className="text-sm text-gray-600">
                                  per night
                                </div>
                              </div>
                            </div>

                            {offer.room && (
                              <div className="text-sm text-gray-600 mb-3">
                                {offer.room.typeEstimated?.category || offer.room.type}
                                {offer.room.typeEstimated?.beds && (
                                  <span> • {offer.room.typeEstimated.beds} beds</span>
                                )}
                              </div>
                            )}

                            {selectedItinerary && (
                              <button
                                onClick={() => addHotelToItinerary(hotel, offer)}
                                className="w-full flex items-center justify-center bg-green-600 text-white py-2 rounded hover:bg-green-700"
                              >
                                <Plus className="w-4 h-4 mr-1" />
                                Add to Trip
                              </button>
                            )}
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Pagination */}
          {pagination.totalPages > 1 && (
            <div className="p-6 border-t border-gray-200">
              <div className="flex justify-center gap-2">
                <button
                  onClick={() => searchHotels(pagination.page - 1)}
                  disabled={pagination.page === 0}
                  className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 hover:bg-gray-50"
                >
                  Previous
                </button>
                <span className="px-4 py-2">
                  Page {pagination.page + 1} of {pagination.totalPages}
                </span>
                <button
                  onClick={() => searchHotels(pagination.page + 1)}
                  disabled={pagination.page >= pagination.totalPages - 1}
                  className="px-4 py-2 border border-gray-300 rounded-lg disabled:opacity-50 hover:bg-gray-50"
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Popular Destinations */}
      {hotels.length === 0 && !loading && popularDestinations.length > 0 && (
        <div className="bg-white rounded-lg shadow-lg p-6">
          <h3 className="text-xl font-semibold text-gray-800 mb-4">Popular Destinations</h3>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
            {popularDestinations.slice(0, 8).map((destination, index) => (
              <button
                key={index}
                onClick={() => setSearchForm(prev => ({ ...prev, cityCode: destination }))}
                className="p-3 border border-gray-300 rounded-lg hover:bg-blue-50 hover:border-blue-300 text-left"
              >
                <div className="font-medium text-sm">{destination}</div>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default HotelSearch;
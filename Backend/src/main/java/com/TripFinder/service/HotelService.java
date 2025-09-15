package com.TripFinder.service;

import com.TripFinder.dto.HotelSearchRequest;
import com.TripFinder.dto.HotelResponse;
import com.TripFinder.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for hotel search operations
 */
public interface HotelService {
    
    /**
     * Legacy method for getting all hotels from database
     */
    List<Hotel> getAllHotels();
    
    /**
     * Search for hotels using Amadeus API with pagination
     *
     * @param searchRequest Hotel search parameters
     * @param pageable Pagination parameters
     * @return Paginated hotel results
     */
    Page<HotelResponse> searchHotels(HotelSearchRequest searchRequest, Pageable pageable);
    
    /**
     * Get hotel details by offer ID
     *
     * @param hotelId Amadeus hotel ID
     * @param offerId Specific offer ID
     * @return Hotel details with offers
     */
    HotelResponse getHotelDetails(String hotelId, String offerId);
    
    /**
     * Get hotels by city or location
     *
     * @param cityCode IATA city code
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @param adults Number of adults
     * @return List of hotels
     */
    List<HotelResponse> getHotelsByLocation(String cityCode, String checkIn, String checkOut, Integer adults);
    
    /**
     * Get popular hotel destinations
     *
     * @return List of popular city codes
     */
    List<String> getPopularDestinations();
    
    /**
     * Clear hotel cache
     */
    void clearHotelCache();
}

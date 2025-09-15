package com.TripFinder.service;

import com.TripFinder.dto.FlightSearchRequest;
import com.TripFinder.dto.FlightResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for flight search operations
 */
public interface FlightService {

    /**
     * Search for flights using Amadeus API with pagination
     *
     * @param searchRequest Flight search parameters
     * @param pageable Pagination parameters
     * @return Paginated flight results
     */
    Page<FlightResponse> searchFlights(FlightSearchRequest searchRequest, Pageable pageable);

    /**
     * Get flight details by offer ID
     *
     * @param offerId Amadeus flight offer ID
     * @return Flight details
     */
    FlightResponse getFlightDetails(String offerId);

    /**
     * Get available flight destinations
     *
     * @param origin Origin airport code
     * @return List of available destinations
     */
    List<String> getFlightDestinations(String origin);

    /**
     * Get popular flight routes
     *
     * @return List of popular routes
     */
    List<String> getPopularRoutes();

    /**
     * Clear flight cache
     */
    void clearFlightCache();
}
package com.TripFinder.controller;

import com.TripFinder.dto.FlightSearchRequest;
import com.TripFinder.dto.FlightResponse;
import com.TripFinder.service.FlightService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for flight search operations using Amadeus API
 */
@RestController
@RequestMapping("/api/v1/flights")
@CrossOrigin(origins = "${frontend.url:http://localhost:5173}")
public class FlightController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    
    @Autowired
    private FlightService flightService;
    
    /**
     * Search for flights with pagination and filtering
     *
     * @param searchRequest Flight search parameters
     * @param page Page number (0-based)
     * @param size Page size (max 50)
     * @param sortBy Sort field (price, duration, departure_time)
     * @param sortOrder Sort order (asc, desc)
     * @return Paginated flight results
     */
    @PostMapping("/search")
    public ResponseEntity<Page<FlightResponse>> searchFlights(
            @Valid @RequestBody FlightSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        
        logger.info("Flight search request: {} -> {} on {}", 
                searchRequest.getOrigin(), searchRequest.getDestination(), searchRequest.getDepartureDate());
        
        // Override request pagination with URL params if provided
        searchRequest.setPage(page + 1); // Convert to 1-based
        searchRequest.setSize(Math.min(size, 50)); // Cap at 50
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortOrder(sortOrder);
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        Page<FlightResponse> results = flightService.searchFlights(searchRequest, pageable);
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Legacy GET endpoint for backward compatibility
     */
    @GetMapping
    public ResponseEntity<Page<FlightResponse>> getFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date,
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "0") int children,
            @RequestParam(defaultValue = "ECONOMY") String travelClass,
            @RequestParam(defaultValue = "false") boolean nonStop,
            @RequestParam(defaultValue = "USD") String currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        FlightSearchRequest searchRequest = FlightSearchRequest.builder()
                .origin(from)
                .destination(to)
                .departureDate(LocalDate.parse(date))
                .adults(adults)
                .children(children)
                .travelClass(travelClass)
                .nonStop(nonStop)
                .currency(currency)
                .page(page + 1)
                .size(Math.min(size, 50))
                .build();
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        Page<FlightResponse> results = flightService.searchFlights(searchRequest, pageable);
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Get flight details by offer ID
     */
    @GetMapping("/{offerId}")
    public ResponseEntity<FlightResponse> getFlightDetails(@PathVariable String offerId) {
        logger.info("Getting flight details for offer: {}", offerId);
        
        FlightResponse flight = flightService.getFlightDetails(offerId);
        return ResponseEntity.ok(flight);
    }
    
    /**
     * Get available destinations from an origin
     */
    @GetMapping("/destinations")
    public ResponseEntity<List<String>> getDestinations(
            @RequestParam String origin) {
        
        List<String> destinations = flightService.getFlightDestinations(origin);
        return ResponseEntity.ok(destinations);
    }
    
    /**
     * Get popular flight routes
     */
    @GetMapping("/popular-routes")
    public ResponseEntity<List<String>> getPopularRoutes() {
        List<String> routes = flightService.getPopularRoutes();
        return ResponseEntity.ok(routes);
    }
    
    /**
     * Clear flight cache (admin endpoint)
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        flightService.clearFlightCache();
        return ResponseEntity.ok("Flight cache cleared successfully");
    }
}

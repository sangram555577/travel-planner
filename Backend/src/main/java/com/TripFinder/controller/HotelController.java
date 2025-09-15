package com.TripFinder.controller;

import com.TripFinder.dto.HotelSearchRequest;
import com.TripFinder.dto.HotelResponse;
import com.TripFinder.entity.Hotel;
import com.TripFinder.service.HotelService;
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
 * REST controller for hotel search operations using Amadeus API
 */
@RestController
@RequestMapping("/api/v1/hotels")
@CrossOrigin(origins = "${frontend.url:http://localhost:5173}")
public class HotelController {
    
    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);
    
    @Autowired
    private HotelService hotelService;
    
    /**
     * Legacy endpoint for getting all hotels from database
     */
    @GetMapping("/all")
    public ResponseEntity<List<Hotel>> getAllHotels() {
        List<Hotel> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }
    
    /**
     * Search for hotels with pagination and filtering
     *
     * @param searchRequest Hotel search parameters
     * @param page Page number (0-based)
     * @param size Page size (max 50)
     * @param sortBy Sort field (price, rating, distance)
     * @param sortOrder Sort order (asc, desc)
     * @return Paginated hotel results
     */
    @PostMapping("/search")
    public ResponseEntity<Page<HotelResponse>> searchHotels(
            @Valid @RequestBody HotelSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        
        logger.info("Hotel search request: {} from {} to {}", 
                searchRequest.getCityCode(), searchRequest.getCheckInDate(), searchRequest.getCheckOutDate());
        
        // Override request pagination with URL params if provided
        searchRequest.setPage(page + 1); // Convert to 1-based
        searchRequest.setSize(Math.min(size, 50)); // Cap at 50
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortOrder(sortOrder);
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        Page<HotelResponse> results = hotelService.searchHotels(searchRequest, pageable);
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * GET endpoint for hotel search (backward compatibility)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<HotelResponse>> getHotels(
            @RequestParam String cityCode,
            @RequestParam String checkIn,
            @RequestParam String checkOut,
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "1") int rooms,
            @RequestParam(defaultValue = "USD") String currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        
        HotelSearchRequest searchRequest = HotelSearchRequest.builder()
                .cityCode(cityCode)
                .checkInDate(LocalDate.parse(checkIn))
                .checkOutDate(LocalDate.parse(checkOut))
                .adults(adults)
                .rooms(rooms)
                .currency(currency)
                .page(page + 1)
                .size(Math.min(size, 50))
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .build();
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        Page<HotelResponse> results = hotelService.searchHotels(searchRequest, pageable);
        
        return ResponseEntity.ok(results);
    }
    
    /**
     * Get hotel details by hotel ID and offer ID
     */
    @GetMapping("/{hotelId}/offers/{offerId}")
    public ResponseEntity<HotelResponse> getHotelDetails(
            @PathVariable String hotelId, 
            @PathVariable String offerId) {
        
        logger.info("Getting hotel details for hotel: {} and offer: {}", hotelId, offerId);
        
        HotelResponse hotel = hotelService.getHotelDetails(hotelId, offerId);
        return ResponseEntity.ok(hotel);
    }
    
    /**
     * Get hotels by location (simplified endpoint)
     */
    @GetMapping("/location")
    public ResponseEntity<List<HotelResponse>> getHotelsByLocation(
            @RequestParam String cityCode,
            @RequestParam String checkIn,
            @RequestParam String checkOut,
            @RequestParam(defaultValue = "1") int adults) {
        
        List<HotelResponse> hotels = hotelService.getHotelsByLocation(cityCode, checkIn, checkOut, adults);
        return ResponseEntity.ok(hotels);
    }
    
    /**
     * Get popular hotel destinations
     */
    @GetMapping("/popular-destinations")
    public ResponseEntity<List<String>> getPopularDestinations() {
        List<String> destinations = hotelService.getPopularDestinations();
        return ResponseEntity.ok(destinations);
    }
    
    /**
     * Clear hotel cache (admin endpoint)
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        hotelService.clearHotelCache();
        return ResponseEntity.ok("Hotel cache cleared successfully");
    }
}

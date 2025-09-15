package com.TripFinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for hotel search requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSearchRequest {

    @NotBlank(message = "City code is required")
    private String cityCode; // IATA city code (e.g., "PAR", "NYC")

    private String latitude;
    private String longitude;
    private String radius = "5"; // Search radius in KM

    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Adults must be at least 1")
    @Max(value = 9, message = "Adults cannot exceed 9")
    private Integer adults = 1;

    @Min(value = 0, message = "Rooms cannot be negative")
    @Max(value = 9, message = "Rooms cannot exceed 9")
    private Integer rooms = 1;

    private String currency = "USD";

    @Min(value = 1, message = "Page must be at least 1")
    private Integer page = 1;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 50, message = "Size cannot exceed 50")
    private Integer size = 20;

    // Filtering options
    private List<String> hotelIds;
    
    private List<String> amenities; // SWIMMING_POOL, SPA, FITNESS_CENTER, etc.
    
    private List<Integer> ratings; // 1-5 star ratings
    
    private Double priceMin;
    
    private Double priceMax;
    
    private String sortBy = "price"; // price, distance, rating
    
    private String sortOrder = "asc"; // asc, desc
    
    private String lang = "EN"; // Language for results
    
    private Boolean includeClosed = false; // Include temporarily closed hotels
    
    private Boolean bestRateOnly = true; // Return only best rate per hotel
}
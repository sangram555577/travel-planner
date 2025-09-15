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

/**
 * DTO for flight search requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchRequest {

    @NotBlank(message = "Origin is required")
    private String origin; // Airport code (e.g., "JFK", "LAX")

    @NotBlank(message = "Destination is required")
    private String destination; // Airport code (e.g., "JFK", "LAX")

    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;

    private LocalDate returnDate; // Optional for round trip

    @Min(value = 1, message = "Adults must be at least 1")
    @Max(value = 9, message = "Adults cannot exceed 9")
    private Integer adults = 1;

    @Min(value = 0, message = "Children cannot be negative")
    @Max(value = 9, message = "Children cannot exceed 9")
    private Integer children = 0;

    @Min(value = 0, message = "Infants cannot be negative")
    @Max(value = 9, message = "Infants cannot exceed 9")
    private Integer infants = 0;

    private String travelClass = "ECONOMY"; // ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST

    private Boolean nonStop = false;

    private String currency = "USD";

    @Min(value = 1, message = "Page must be at least 1")
    private Integer page = 1;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 50, message = "Size cannot exceed 50")
    private Integer size = 10;

    // Sorting and filtering
    private String sortBy = "price"; // price, duration, departure_time

    private String sortOrder = "asc"; // asc, desc

    private Double maxPrice;

    private Integer maxDuration; // in minutes

    private String airline; // airline code filter
}
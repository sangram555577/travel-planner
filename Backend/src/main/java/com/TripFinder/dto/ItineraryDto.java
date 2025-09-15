package com.TripFinder.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO for creating or updating an itinerary.
 */
public record ItineraryDto(
    @NotBlank(message = "Trip name cannot be blank")
    String tripName,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must not be in the past")
    LocalDate endDate,

    String activitiesJson,

    @NotNull(message = "User ID cannot be null")
    Integer userId
) {}
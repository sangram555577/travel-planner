package com.TripFinder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * DTO for itinerary item operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryItemDto {

    private Long id;

    @NotNull(message = "Position is required")
    private Integer position;

    @NotBlank(message = "Type is required")
    private String type; // "flight", "hotel", "activity"

    @NotBlank(message = "Provider is required")
    private String provider; // "amadeus", "local"

    private String meta; // JSON string with item-specific data

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Double price;

    private String currency;

    @NotNull(message = "Itinerary ID is required")
    private Long itineraryId;
}

/**
 * DTO for reordering itinerary items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ReorderRequest {
    
    @NotNull(message = "Itinerary ID is required")
    private Long itineraryId;
    
    @NotNull(message = "Item order is required")
    private java.util.List<ItemOrder> itemOrders;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemOrder {
        @NotNull(message = "Item ID is required")
        private Long itemId;
        
        @NotNull(message = "Position is required")
        private Integer position;
    }
}

/**
 * DTO for adding item to itinerary from search results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AddToItineraryRequest {
    
    @NotNull(message = "Itinerary ID is required")
    private Long itineraryId;
    
    @NotBlank(message = "Type is required")
    private String type; // "flight", "hotel"
    
    @NotBlank(message = "Provider is required")
    private String provider; // "amadeus"
    
    @NotBlank(message = "External ID is required")
    private String externalId; // Amadeus offer ID
    
    private String meta; // Additional metadata
    
    private Integer position; // Optional - will append to end if not provided
}
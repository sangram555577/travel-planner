package com.TripFinder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Represents an individual item in an itinerary (flight, hotel, activity, etc.)
 * This entity maps to the 'itinerary_items' table in the database.
 */
@Entity
@Table(name = "itinerary_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Position cannot be null")
    @Column(name = "position", nullable = false)
    private Integer position;

    @NotBlank(message = "Type cannot be blank")
    @Column(name = "type", nullable = false, length = 50)
    private String type; // "flight", "hotel", "activity", etc.

    @NotBlank(message = "Provider cannot be blank")  
    @Column(name = "provider", nullable = false, length = 100)
    private String provider; // "amadeus", "local", etc.

    @Lob
    @Column(name = "meta", columnDefinition = "TEXT")
    private String meta; // JSON string with item-specific data

    @NotBlank(message = "Title cannot be blank")
    @Column(name = "title", nullable = false)
    private String title; // Display title for the item

    @Column(name = "description", length = 500)
    private String description; // Optional description

    @Column(name = "price")
    private Double price; // Optional price information

    @Column(name = "currency", length = 3)
    private String currency; // Currency code (USD, EUR, etc.)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    @NotNull(message = "ItineraryItem must be associated with an Itinerary")
    private Itinerary itinerary;

    /**
     * Convenience method to get metadata as a specific type
     */
    public String getMetadata() {
        return this.meta;
    }

    /**
     * Convenience method to set metadata
     */
    public void setMetadata(String metadata) {
        this.meta = metadata;
    }
}
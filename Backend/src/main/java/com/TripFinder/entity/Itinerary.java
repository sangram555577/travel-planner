package com.TripFinder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a user's trip itinerary.
 * This entity maps to the 'itineraries' table in the database.
 */
@Entity
@Table(name = "itineraries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Trip name cannot be blank")
    @Column(name = "trip_name", nullable = false)
    private String tripName;

    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Lob
    @Column(name = "activities_json", columnDefinition = "TEXT")
    private String activitiesJson; // Store daily activities as a JSON string

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Itinerary must be associated with a user")
    private User user;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<ItineraryItem> items = new ArrayList<>();

    /**
     * Helper method to add an item to the itinerary
     */
    public void addItem(ItineraryItem item) {
        items.add(item);
        item.setItinerary(this);
    }

    /**
     * Helper method to remove an item from the itinerary
     */
    public void removeItem(ItineraryItem item) {
        items.remove(item);
        item.setItinerary(null);
    }
}
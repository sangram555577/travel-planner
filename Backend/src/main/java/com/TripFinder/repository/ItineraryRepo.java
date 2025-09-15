package com.TripFinder.repository;

import com.TripFinder.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Itinerary} entities.
 * Provides standard CRUD operations and custom queries for itinerary data.
 */
@Repository
public interface ItineraryRepo extends JpaRepository<Itinerary, Long> { // <-- This line is now fixed

    /**
     * Finds all itineraries associated with a specific user.
     *
     * @param userId The ID of the user whose itineraries are to be retrieved.
     * @return A list of {@link Itinerary} objects belonging to the specified user.
     */
    List<Itinerary> findByUserId(int userId);

    /**
     * Counts the number of itineraries for a specific user.
     *
     * @param userId The ID of the user.
     * @return The number of itineraries belonging to the specified user.
     */
    long countByUserId(int userId);
}
package com.TripFinder.service;

import com.TripFinder.entity.Destination;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing destination-related business logic.
 */
public interface DestinationService {

    /**
     * Retrieves all destinations.
     *
     * @return A list of all Destination entities.
     */
    List<Destination> getAllDestinations();

    /**
     * Retrieves a single destination by its ID.
     *
     * @param id The ID of the destination.
     * @return An Optional containing the Destination if found, or empty otherwise.
     */
    Optional<Destination> getDestinationById(int id);
}
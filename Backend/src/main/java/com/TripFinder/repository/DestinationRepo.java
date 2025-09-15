package com.TripFinder.repository;

import com.TripFinder.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Destination} entities.
 * Provides standard CRUD operations for destination data.
 */
@Repository
public interface DestinationRepo extends JpaRepository<Destination, Integer> {
}
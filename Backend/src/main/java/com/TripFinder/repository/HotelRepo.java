package com.TripFinder.repository;

import com.TripFinder.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Hotel} entities.
 * Provides standard CRUD operations for hotel data.
 */
@Repository
public interface HotelRepo extends JpaRepository<Hotel, Integer> {
}
package com.TripFinder.repository;

import com.TripFinder.entity.ItineraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ItineraryItem entity
 */
@Repository
public interface ItineraryItemRepo extends JpaRepository<ItineraryItem, Long> {

    /**
     * Find all items for a specific itinerary, ordered by position
     */
    @Query("SELECT i FROM ItineraryItem i WHERE i.itinerary.id = :itineraryId ORDER BY i.position")
    List<ItineraryItem> findByItineraryIdOrderByPosition(@Param("itineraryId") Long itineraryId);

    /**
     * Find items by itinerary ID and type
     */
    @Query("SELECT i FROM ItineraryItem i WHERE i.itinerary.id = :itineraryId AND i.type = :type ORDER BY i.position")
    List<ItineraryItem> findByItineraryIdAndTypeOrderByPosition(
            @Param("itineraryId") Long itineraryId, 
            @Param("type") String type
    );

    /**
     * Find the maximum position for an itinerary (for appending new items)
     */
    @Query("SELECT COALESCE(MAX(i.position), 0) FROM ItineraryItem i WHERE i.itinerary.id = :itineraryId")
    Integer findMaxPositionByItineraryId(@Param("itineraryId") Long itineraryId);

    /**
     * Count items in an itinerary
     */
    @Query("SELECT COUNT(i) FROM ItineraryItem i WHERE i.itinerary.id = :itineraryId")
    Long countByItineraryId(@Param("itineraryId") Long itineraryId);

    /**
     * Delete items by itinerary ID
     */
    void deleteByItineraryId(Long itineraryId);
}
package com.TripFinder.service;

import com.TripFinder.dto.ItineraryDto;
import com.TripFinder.dto.ItineraryItemDto;
import com.TripFinder.entity.Itinerary;
import com.TripFinder.entity.ItineraryItem;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing itinerary-related business logic with item support
 */
public interface ItineraryService {
    
    // Basic itinerary operations
    Itinerary saveItinerary(ItineraryDto itineraryDto);
    List<Itinerary> getItinerariesByUserId(int userId);
    List<Itinerary> getAllItineraries(); // Admin method
    Itinerary getItineraryById(Long id);
    Itinerary updateItinerary(Long id, ItineraryDto itineraryDto);
    void deleteItinerary(Long id);
    
    // Item management
    ItineraryItem addItemToItinerary(ItineraryItemDto itemDto);
    ItineraryItem addItemFromExternalSearch(Long itineraryId, String type, String provider, 
                                          String externalId, String metadata, Integer position);
    List<ItineraryItem> getItineraryItems(Long itineraryId);
    List<ItineraryItem> getItineraryItemsByType(Long itineraryId, String type);
    ItineraryItem updateItineraryItem(ItineraryItemDto itemDto);
    void removeItemFromItinerary(Long itineraryId, Long itemId);
    
    // Reordering and positioning
    List<ItineraryItem> reorderItineraryItems(Long itineraryId, List<Map<String, Object>> itemOrders);
    ItineraryItem moveItemToPosition(Long itineraryId, Long itemId, int newPosition);
    
    // Statistics and metadata
    Map<String, Object> getItineraryStatistics(Long itineraryId);
}

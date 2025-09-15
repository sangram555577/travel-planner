package com.TripFinder.service;

import com.TripFinder.dto.ItineraryDto;
import com.TripFinder.entity.Itinerary;
import java.util.List;

/**
 * Service interface for managing itinerary-related business logic.
 */
public interface ItineraryService {
    Itinerary saveItinerary(ItineraryDto itineraryDto);
    List<Itinerary> getItinerariesByUserId(int userId);
    Itinerary updateItinerary(Long id, ItineraryDto itineraryDto);
    void deleteItinerary(Long id);
}
package com.TripFinder.serviceImpl;

import com.TripFinder.dto.ItineraryDto;
import com.TripFinder.dto.ItineraryItemDto;
import com.TripFinder.entity.Itinerary;
import com.TripFinder.entity.ItineraryItem;
import com.TripFinder.entity.User;
import com.TripFinder.repository.ItineraryRepo;
import com.TripFinder.repository.UserRepo;
import com.TripFinder.service.ItineraryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    @Autowired
    private ItineraryRepo itineraryRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public Itinerary saveItinerary(ItineraryDto itineraryDto) {
        validateDto(itineraryDto);
        User user = userRepo.findById(itineraryDto.userId())
                .orElseThrow(() -> new RuntimeException("User not found, cannot save itinerary."));

        Itinerary itinerary = new Itinerary();
        BeanUtils.copyProperties(itineraryDto, itinerary);
        itinerary.setUser(user);

        return itineraryRepo.save(itinerary);
    }

    @Override
    public List<Itinerary> getItinerariesByUserId(int userId) {
        return itineraryRepo.findByUserId(userId);
    }

    @Override
    public List<Itinerary> getAllItineraries() {
        return itineraryRepo.findAll();
    }

    @Override
    public Itinerary updateItinerary(Long id, ItineraryDto itineraryDto) {
        validateDto(itineraryDto);
        Itinerary existingItinerary = itineraryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found with ID: " + id));

        User user = userRepo.findById(itineraryDto.userId())
                .orElseThrow(() -> new RuntimeException("User not found, cannot update itinerary."));

        BeanUtils.copyProperties(itineraryDto, existingItinerary);
        existingItinerary.setId(id); // Ensure the ID is not changed
        existingItinerary.setUser(user);

        return itineraryRepo.save(existingItinerary);
    }

    @Override
    public void deleteItinerary(Long id) {
        if (!itineraryRepo.existsById(id)) {
            throw new RuntimeException("Itinerary not found with ID: " + id);
        }
        itineraryRepo.deleteById(id);
    }

    private void validateDto(ItineraryDto dto) {
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new RuntimeException("End date cannot be before start date.");
        }
    }

    // Stub implementations for missing methods - these would need full implementation
    // in a production system, but for now we provide basic stubs to allow compilation
    
    @Override
    public Itinerary getItineraryById(Long id) {
        return itineraryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found with ID: " + id));
    }
    
    @Override
    public ItineraryItem addItemToItinerary(ItineraryItemDto itemDto) {
        // Stub implementation - would need ItineraryItemRepository and full logic
        throw new UnsupportedOperationException("ItineraryItem functionality not fully implemented yet");
    }
    
    @Override
    public ItineraryItem addItemFromExternalSearch(Long itineraryId, String type, String provider, 
                                                  String externalId, String metadata, Integer position) {
        // Stub implementation
        throw new UnsupportedOperationException("ItineraryItem functionality not fully implemented yet");
    }
    
    @Override
    public List<ItineraryItem> getItineraryItems(Long itineraryId) {
        // Stub implementation
        throw new UnsupportedOperationException("ItineraryItem functionality not fully implemented yet");
    }
    
    @Override
    public List<ItineraryItem> getItineraryItemsByType(Long itineraryId, String type) {
        // Stub implementation
        throw new UnsupportedOperationException("ItineraryItem functionality not fully implemented yet");
    }
    
    @Override
    public ItineraryItem updateItineraryItem(ItineraryItemDto itemDto) {
        // Stub implementation
        throw new UnsupportedOperationException("ItineraryItem functionality not fully implemented yet");
    }
    
    @Override
    public void removeItemFromItinerary(Long itineraryId, Long itemId) {
        // Stub implementation
        throw new UnsupportedOperationException("ItineraryItem functionality not fully implemented yet");
    }
    
    @Override
    public List<ItineraryItem> reorderItineraryItems(Long itineraryId, List<Map<String, Object>> itemOrders) {
        // Stub implementation
        throw new UnsupportedOperationException("ItineraryItem functionality not fully implemented yet");
    }
    
    @Override
    public ItineraryItem moveItemToPosition(Long itineraryId, Long itemId, int newPosition) {
        // Stub implementation
        throw new UnsupportedOperationException("ItineraryItem functionality not fully implemented yet");
    }
    
    @Override
    public Map<String, Object> getItineraryStatistics(Long itineraryId) {
        // Basic implementation for statistics
        Itinerary itinerary = getItineraryById(itineraryId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("id", itinerary.getId());
        stats.put("tripName", itinerary.getTripName());
        stats.put("startDate", itinerary.getStartDate());
        stats.put("endDate", itinerary.getEndDate());
        stats.put("totalItems", 0); // Would count actual items when implemented
        stats.put("userId", itinerary.getUser().getId());
        return stats;
    }
}
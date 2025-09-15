package com.TripFinder.controller;

import com.TripFinder.dto.ItineraryDto;
import com.TripFinder.dto.ItineraryItemDto;
import com.TripFinder.entity.Itinerary;
import com.TripFinder.entity.ItineraryItem;
import com.TripFinder.service.ItineraryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for itinerary management with drag & drop support
 */
@RestController
@RequestMapping("/api/v1/itineraries")
@CrossOrigin(origins = "${frontend.url:http://localhost:5173}")
public class ItineraryController {
    
    private static final Logger logger = LoggerFactory.getLogger(ItineraryController.class);
    
    @Autowired
    private ItineraryService itineraryService;
    
    /**
     * Create a new itinerary
     */
    @PostMapping
    public ResponseEntity<Itinerary> saveItinerary(@Valid @RequestBody ItineraryDto itineraryDto) {
        logger.info("Creating new itinerary: {}", itineraryDto.tripName());
        Itinerary savedItinerary = itineraryService.saveItinerary(itineraryDto);
        return new ResponseEntity<>(savedItinerary, HttpStatus.CREATED);
    }
    
    /**
     * Get itineraries by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Itinerary>> getItinerariesByUserId(@PathVariable int userId) {
        List<Itinerary> itineraries = itineraryService.getItinerariesByUserId(userId);
        return ResponseEntity.ok(itineraries);
    }
    
    /**
     * Get a specific itinerary with all items
     */
    @GetMapping("/{id}")
    public ResponseEntity<Itinerary> getItinerary(@PathVariable Long id) {
        logger.info("Getting itinerary: {}", id);
        Itinerary itinerary = itineraryService.getItineraryById(id);
        return ResponseEntity.ok(itinerary);
    }
    
    /**
     * Update an existing itinerary
     */
    @PutMapping("/{id}")
    public ResponseEntity<Itinerary> updateItinerary(
            @PathVariable Long id, 
            @Valid @RequestBody ItineraryDto itineraryDto) {
        
        logger.info("Updating itinerary: {}", id);
        Itinerary updatedItinerary = itineraryService.updateItinerary(id, itineraryDto);
        return ResponseEntity.ok(updatedItinerary);
    }
    
    /**
     * Delete an itinerary
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItinerary(@PathVariable Long id) {
        logger.info("Deleting itinerary: {}", id);
        itineraryService.deleteItinerary(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Add an item to an itinerary
     */
    @PostMapping("/{id}/items")
    public ResponseEntity<ItineraryItem> addItemToItinerary(
            @PathVariable Long id,
            @Valid @RequestBody ItineraryItemDto itemDto) {
        
        logger.info("Adding item to itinerary {}: {} - {}", id, itemDto.getType(), itemDto.getTitle());
        itemDto.setItineraryId(id);
        ItineraryItem item = itineraryService.addItemToItinerary(itemDto);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }
    
    /**
     * Add item from external search results (flights/hotels)
     */
    @PostMapping("/{id}/items/from-search")
    public ResponseEntity<ItineraryItem> addItemFromSearch(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        String type = (String) request.get("type");
        String provider = (String) request.get("provider");
        String externalId = (String) request.get("externalId");
        String metadata = (String) request.get("metadata");
        Integer position = (Integer) request.get("position");
        
        logger.info("Adding {} item from {} to itinerary {}", type, provider, id);
        
        ItineraryItem item = itineraryService.addItemFromExternalSearch(
                id, type, provider, externalId, metadata, position
        );
        
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }
    
    /**
     * Get all items for an itinerary
     */
    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItineraryItem>> getItineraryItems(@PathVariable Long id) {
        List<ItineraryItem> items = itineraryService.getItineraryItems(id);
        return ResponseEntity.ok(items);
    }
    
    /**
     * Update an itinerary item
     */
    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<ItineraryItem> updateItineraryItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody ItineraryItemDto itemDto) {
        
        logger.info("Updating item {} in itinerary {}", itemId, id);
        itemDto.setId(itemId);
        itemDto.setItineraryId(id);
        ItineraryItem item = itineraryService.updateItineraryItem(itemDto);
        return ResponseEntity.ok(item);
    }
    
    /**
     * Delete an item from an itinerary
     */
    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromItinerary(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        
        logger.info("Removing item {} from itinerary {}", itemId, id);
        itineraryService.removeItemFromItinerary(id, itemId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Reorder itinerary items (for drag & drop)
     */
    @PostMapping("/{id}/items/reorder")
    public ResponseEntity<List<ItineraryItem>> reorderItems(
            @PathVariable Long id,
            @RequestBody List<Map<String, Object>> itemOrders) {
        
        logger.info("Reordering {} items in itinerary {}", itemOrders.size(), id);
        
        List<ItineraryItem> reorderedItems = itineraryService.reorderItineraryItems(id, itemOrders);
        return ResponseEntity.ok(reorderedItems);
    }
    
    /**
     * Move item to different position
     */
    @PostMapping("/{id}/items/{itemId}/move")
    public ResponseEntity<ItineraryItem> moveItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestParam int newPosition) {
        
        logger.info("Moving item {} to position {} in itinerary {}", itemId, newPosition, id);
        
        ItineraryItem movedItem = itineraryService.moveItemToPosition(id, itemId, newPosition);
        return ResponseEntity.ok(movedItem);
    }
    
    /**
     * Get items by type for an itinerary
     */
    @GetMapping("/{id}/items/type/{type}")
    public ResponseEntity<List<ItineraryItem>> getItemsByType(
            @PathVariable Long id,
            @PathVariable String type) {
        
        List<ItineraryItem> items = itineraryService.getItineraryItemsByType(id, type);
        return ResponseEntity.ok(items);
    }
    
    /**
     * Get itinerary statistics
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getItineraryStats(@PathVariable Long id) {
        Map<String, Object> stats = itineraryService.getItineraryStatistics(id);
        return ResponseEntity.ok(stats);
    }
}

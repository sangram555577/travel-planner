package com.TripFinder.controller;

import com.TripFinder.dto.ItineraryDto;
import com.TripFinder.entity.Itinerary;
import com.TripFinder.service.ItineraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for authenticated itinerary-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/itineraries")
@CrossOrigin(origins = "http://localhost:5173")
public class ItineraryController {

    @Autowired
    private ItineraryService itineraryService;

    @PostMapping
    public ResponseEntity<Itinerary> saveItinerary(@Valid @RequestBody ItineraryDto itineraryDto) {
        Itinerary savedItinerary = itineraryService.saveItinerary(itineraryDto);
        return new ResponseEntity<>(savedItinerary, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Itinerary>> getItinerariesByUserId(@PathVariable int userId) {
        List<Itinerary> itineraries = itineraryService.getItinerariesByUserId(userId);
        return ResponseEntity.ok(itineraries);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Itinerary> updateItinerary(@PathVariable Long id, @Valid @RequestBody ItineraryDto itineraryDto) {
        Itinerary updatedItinerary = itineraryService.updateItinerary(id, itineraryDto);
        return ResponseEntity.ok(updatedItinerary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItinerary(@PathVariable Long id) {
        itineraryService.deleteItinerary(id);
        return ResponseEntity.noContent().build();
    }
}
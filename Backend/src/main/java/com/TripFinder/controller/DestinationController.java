package com.TripFinder.controller;

import com.TripFinder.entity.Destination;
import com.TripFinder.service.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for public destination-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/destinations")
@CrossOrigin(origins = "http://localhost:5173")
public class DestinationController {

    @Autowired
    private DestinationService destinationService;

    /**
     * Get all destinations.
     *
     * @return A ResponseEntity containing a list of all destinations.
     */
    @GetMapping
    public ResponseEntity<List<Destination>> getAllDestinations() {
        List<Destination> destinations = destinationService.getAllDestinations();
        return ResponseEntity.ok(destinations);
    }

    /**
     * Get a specific destination by its ID.
     *
     * @param id The ID of the destination.
     * @return A ResponseEntity containing the destination or a 404 Not Found status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Destination> getDestinationById(@PathVariable int id) {
        return destinationService.getDestinationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Fetches the current weather for a given city using an external API.
     *
     * @param city The name of the city.
     * @return A ResponseEntity containing the weather data or an error response.
     */
    @GetMapping("/weather/{city}")
    public ResponseEntity<?> getWeatherByCity(@PathVariable String city) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 1. Geocoding: Get latitude and longitude from city name
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + city;
            Map geoResponse = restTemplate.getForObject(geoUrl, Map.class);

            if (geoResponse == null || geoResponse.get("results") == null || ((List) geoResponse.get("results")).isEmpty()) {
                throw new RuntimeException("City not found: " + city);
            }

            Map<?, ?> firstResult = ((List<Map<?, ?>>) geoResponse.get("results")).get(0);
            double latitude = (Double) firstResult.get("latitude");
            double longitude = (Double) firstResult.get("longitude");

            // 2. Weather API: Get weather using coordinates
            String weatherUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true",
                latitude, longitude
            );
            Map<?, ?> weatherResponse = restTemplate.getForObject(weatherUrl, Map.class);

            if (weatherResponse == null || !weatherResponse.containsKey("current_weather")) {
                throw new RuntimeException("Weather data unavailable for " + city);
            }

            return ResponseEntity.ok(weatherResponse.get("current_weather"));
        } catch (Exception e) {
            // Let the GlobalExceptionHandler handle this
            throw new RuntimeException("Failed to fetch weather data. Reason: " + e.getMessage());
        }
    }
}
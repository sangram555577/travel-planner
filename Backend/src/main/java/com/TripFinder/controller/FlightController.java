package com.TripFinder.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * REST controller for fetching flight offers from the external Amadeus API.
 */
@RestController
@RequestMapping("/api/v1/flights")
@CrossOrigin(origins = "http://localhost:5173")
public class FlightController {

    @Value("${amadeus.api.key}")
    private String amadeusApiKey;

    @Value("${amadeus.api.secret}")
    private String amadeusApiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Searches for flight offers based on origin, destination, and date.
     *
     * @param from The origin airport code (e.g., "DEL").
     * @param to The destination airport code (e.g., "JAI").
     * @param date The departure date in "YYYY-MM-DD" format.
     * @return A ResponseEntity containing flight data or an error.
     */
    @GetMapping
    public ResponseEntity<?> getFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {
        try {
            // 1. Get Amadeus API Access Token
            String accessToken = getAmadeusAccessToken();

            // 2. Search for flights
            String flightSearchUrl = String.format(
                "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=%s&destinationLocationCode=%s&departureDate=%s&adults=1&nonStop=true",
                from, to, date
            );

            HttpHeaders flightHeaders = new HttpHeaders();
            flightHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> flightRequestEntity = new HttpEntity<>(flightHeaders);

            ResponseEntity<Map> flightResponse = restTemplate.exchange(
                flightSearchUrl, HttpMethod.GET, flightRequestEntity, Map.class
            );

            return ResponseEntity.ok(flightResponse.getBody().get("data"));

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch flight data. Please check parameters and API credentials. Reason: " + e.getMessage());
        }
    }

    /**
     * Authenticates with the Amadeus API to get an access token.
     *
     * @return The access token as a String.
     */
    private String getAmadeusAccessToken() {
        String tokenUrl = "https://test.api.amadeus.com/v1/security/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", amadeusApiKey);
        body.add("client_secret", amadeusApiSecret);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, requestEntity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null || !response.getBody().containsKey("access_token")) {
            throw new RuntimeException("Could not obtain Amadeus access token");
        }
        return response.getBody().get("access_token").toString();
    }
}
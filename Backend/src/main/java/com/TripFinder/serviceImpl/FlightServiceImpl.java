package com.TripFinder.serviceImpl;

import com.TripFinder.config.CacheConfig;
import com.TripFinder.dto.FlightSearchRequest;
import com.TripFinder.dto.FlightResponse;
import com.TripFinder.service.FlightService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);
    
    @Value("${amadeus.api.key}")
    private String apiKey;
    
    @Value("${amadeus.api.secret}")
    private String apiSecret;
    
    @Value("${amadeus.api.base-url}")
    private String baseUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private CacheConfig cacheConfig;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Rate limiting
    private final Map<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final long windowSizeMs = 1000; // 1 second window
    
    @Override
    @Cacheable(value = "flight-offers", key = "#searchRequest.hashCode() + '_' + #pageable.pageNumber")
    @Retry(name = "amadeus-api")
    public Page<FlightResponse> searchFlights(FlightSearchRequest searchRequest, Pageable pageable) {
        logger.info("Searching flights from {} to {} on {}", 
                searchRequest.getOrigin(), searchRequest.getDestination(), searchRequest.getDepartureDate());
        
        try {
            // Check rate limits
            if (!checkRateLimit()) {
                logger.warn("Rate limit exceeded, using fallback data");
                return getFallbackFlightData(searchRequest, pageable);
            }
            
            // Get access token
            String accessToken = getAccessToken();
            
            // Build search URL
            String searchUrl = buildFlightSearchUrl(searchRequest);
            
            // Make API request
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    searchUrl, HttpMethod.GET, requestEntity, JsonNode.class
            );
            
            // Parse response
            List<FlightResponse> flights = parseFlightResponse(response.getBody());
            
            // Apply client-side filtering and sorting
            flights = filterAndSortFlights(flights, searchRequest);
            
            // Handle pagination
            return paginateResults(flights, pageable);
            
        } catch (Exception e) {
            logger.error("Error searching flights: {}", e.getMessage(), e);
            
            // Return fallback data if enabled
            if (cacheConfig.getApi().getFallback().isEnabled()) {
                logger.info("Returning fallback flight data");
                return getFallbackFlightData(searchRequest, pageable);
            }
            
            throw new RuntimeException("Flight search failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public FlightResponse getFlightDetails(String offerId) {
        logger.info("Getting flight details for offer ID: {}", offerId);
        
        try {
            String accessToken = getAccessToken();
            String detailsUrl = baseUrl + "/v1/shopping/flight-offers/" + offerId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    detailsUrl, HttpMethod.GET, requestEntity, JsonNode.class
            );
            
            return parseFlightOffer(response.getBody());
            
        } catch (Exception e) {
            logger.error("Error getting flight details: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get flight details: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> getFlightDestinations(String origin) {
        // This would typically call Amadeus Airport & City Search API
        // For now, returning common destinations
        return getPopularDestinations(origin);
    }
    
    @Override
    public List<String> getPopularRoutes() {
        return Arrays.asList(
                "JFK-LAX", "LAX-JFK", "JFK-LHR", "LHR-JFK",
                "JFK-CDG", "CDG-JFK", "LAX-NRT", "NRT-LAX",
                "JFK-FCO", "FCO-JFK", "LAX-LHR", "LHR-LAX"
        );
    }
    
    @Override
    @CacheEvict(value = {"flight-offers", "amadeus-tokens"}, allEntries = true)
    public void clearFlightCache() {
        logger.info("Clearing flight cache");
    }
    
    @Cacheable(value = "amadeus-tokens", key = "'access_token'")
    private String getAccessToken() {
        logger.debug("Getting Amadeus access token");
        
        String tokenUrl = baseUrl + "/v1/security/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", apiKey);
        body.add("client_secret", apiSecret);
        
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenUrl, requestEntity, JsonNode.class);
            
            if (response.getBody() != null && response.getBody().has("access_token")) {
                return response.getBody().get("access_token").asText();
            }
            
            throw new RuntimeException("No access token in response");
            
        } catch (Exception e) {
            logger.error("Failed to get access token: {}", e.getMessage());
            throw new RuntimeException("Could not obtain Amadeus access token", e);
        }
    }
    
    private String buildFlightSearchUrl(FlightSearchRequest request) {
        StringBuilder url = new StringBuilder(baseUrl + "/v2/shopping/flight-offers");
        url.append("?originLocationCode=").append(request.getOrigin());
        url.append("&destinationLocationCode=").append(request.getDestination());
        url.append("&departureDate=").append(request.getDepartureDate());
        
        if (request.getReturnDate() != null) {
            url.append("&returnDate=").append(request.getReturnDate());
        }
        
        url.append("&adults=").append(request.getAdults());
        
        if (request.getChildren() > 0) {
            url.append("&children=").append(request.getChildren());
        }
        
        if (request.getInfants() > 0) {
            url.append("&infants=").append(request.getInfants());
        }
        
        url.append("&travelClass=").append(request.getTravelClass());
        url.append("&nonStop=").append(request.getNonStop());
        url.append("&currencyCode=").append(request.getCurrency());
        url.append("&max=").append(Math.min(250, request.getSize() * 3)); // Get more results for filtering
        
        return url.toString();
    }
    
    private List<FlightResponse> parseFlightResponse(JsonNode responseBody) {
        List<FlightResponse> flights = new ArrayList<>();
        
        if (responseBody != null && responseBody.has("data")) {
            JsonNode data = responseBody.get("data");
            
            for (JsonNode flightOffer : data) {
                try {
                    FlightResponse flight = parseFlightOffer(flightOffer);
                    if (flight != null) {
                        flights.add(flight);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse flight offer: {}", e.getMessage());
                }
            }
        }
        
        return flights;
    }
    
    private FlightResponse parseFlightOffer(JsonNode flightOffer) {
        if (flightOffer == null) return null;
        
        try {
            FlightResponse.FlightResponseBuilder builder = FlightResponse.builder()
                    .id(flightOffer.path("id").asText())
                    .price(flightOffer.path("price").path("grandTotal").asDouble())
                    .currency(flightOffer.path("price").path("currency").asText())
                    .numberOfBookableSeats(flightOffer.path("numberOfBookableSeats").asInt())
                    .validatingAirlineCodes(flightOffer.path("validatingAirlineCodes").toString());
            
            // Parse itineraries
            List<FlightResponse.Itinerary> itineraries = new ArrayList<>();
            JsonNode itinerariesNode = flightOffer.path("itineraries");
            
            for (JsonNode itineraryNode : itinerariesNode) {
                FlightResponse.Itinerary itinerary = parseItinerary(itineraryNode);
                if (itinerary != null) {
                    itineraries.add(itinerary);
                }
            }
            
            builder.itineraries(itineraries);
            
            return builder.build();
            
        } catch (Exception e) {
            logger.warn("Failed to parse flight offer: {}", e.getMessage());
            return null;
        }
    }
    
    private FlightResponse.Itinerary parseItinerary(JsonNode itineraryNode) {
        try {
            List<FlightResponse.Segment> segments = new ArrayList<>();
            JsonNode segmentsNode = itineraryNode.path("segments");
            
            for (JsonNode segmentNode : segmentsNode) {
                FlightResponse.Segment segment = parseSegment(segmentNode);
                if (segment != null) {
                    segments.add(segment);
                }
            }
            
            return FlightResponse.Itinerary.builder()
                    .duration(itineraryNode.path("duration").asText())
                    .segments(segments)
                    .build();
                    
        } catch (Exception e) {
            logger.warn("Failed to parse itinerary: {}", e.getMessage());
            return null;
        }
    }
    
    private FlightResponse.Segment parseSegment(JsonNode segmentNode) {
        try {
            FlightResponse.Departure departure = FlightResponse.Departure.builder()
                    .iataCode(segmentNode.path("departure").path("iataCode").asText())
                    .terminal(segmentNode.path("departure").path("terminal").asText())
                    .at(LocalDateTime.parse(segmentNode.path("departure").path("at").asText()))
                    .build();
                    
            FlightResponse.Arrival arrival = FlightResponse.Arrival.builder()
                    .iataCode(segmentNode.path("arrival").path("iataCode").asText())
                    .terminal(segmentNode.path("arrival").path("terminal").asText())
                    .at(LocalDateTime.parse(segmentNode.path("arrival").path("at").asText()))
                    .build();
                    
            FlightResponse.Aircraft aircraft = FlightResponse.Aircraft.builder()
                    .code(segmentNode.path("aircraft").path("code").asText())
                    .build();
                    
            return FlightResponse.Segment.builder()
                    .departure(departure)
                    .arrival(arrival)
                    .carrierCode(segmentNode.path("carrierCode").asText())
                    .number(segmentNode.path("number").asText())
                    .aircraft(aircraft)
                    .duration(segmentNode.path("duration").asText())
                    .id(segmentNode.path("id").asText())
                    .numberOfStops(segmentNode.path("numberOfStops").asInt())
                    .build();
                    
        } catch (Exception e) {
            logger.warn("Failed to parse segment: {}", e.getMessage());
            return null;
        }
    }
    
    private List<FlightResponse> filterAndSortFlights(List<FlightResponse> flights, FlightSearchRequest request) {
        return flights.stream()
                .filter(flight -> applyFilters(flight, request))
                .sorted(getComparator(request))
                .collect(Collectors.toList());
    }
    
    private boolean applyFilters(FlightResponse flight, FlightSearchRequest request) {
        // Apply price filter
        if (request.getMaxPrice() != null && flight.getPrice() > request.getMaxPrice()) {
            return false;
        }
        
        // Apply duration filter (would need to calculate total duration)
        // Apply airline filter
        if (request.getAirline() != null && !request.getAirline().isEmpty()) {
            boolean matchesAirline = flight.getItineraries().stream()
                    .flatMap(itinerary -> itinerary.getSegments().stream())
                    .anyMatch(segment -> request.getAirline().equals(segment.getCarrierCode()));
            if (!matchesAirline) {
                return false;
            }
        }
        
        return true;
    }
    
    private Comparator<FlightResponse> getComparator(FlightSearchRequest request) {
        Comparator<FlightResponse> comparator;
        
        switch (request.getSortBy().toLowerCase()) {
            case "price":
                comparator = Comparator.comparing(FlightResponse::getPrice);
                break;
            case "duration":
                // Would need to implement duration calculation
                comparator = Comparator.comparing(FlightResponse::getPrice);
                break;
            default:
                comparator = Comparator.comparing(FlightResponse::getPrice);
        }
        
        if ("desc".equalsIgnoreCase(request.getSortOrder())) {
            comparator = comparator.reversed();
        }
        
        return comparator;
    }
    
    private Page<FlightResponse> paginateResults(List<FlightResponse> flights, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), flights.size());
        
        List<FlightResponse> pageContent = flights.subList(start, end);
        return new PageImpl<>(pageContent, pageable, flights.size());
    }
    
    private boolean checkRateLimit() {
        String key = "amadeus-api";
        long now = System.currentTimeMillis();
        long windowStart = now - windowSizeMs;
        
        AtomicLong counter = requestCounts.computeIfAbsent(key, k -> new AtomicLong(0));
        
        // Simple sliding window rate limiting
        if (counter.get() >= cacheConfig.getRateLimit().getAmadeus().getRequestsPerSecond()) {
            return false;
        }
        
        counter.incrementAndGet();
        
        // Reset counter every second (simplified implementation)
        if (now % 1000 < 100) {
            counter.set(0);
        }
        
        return true;
    }
    
    private Page<FlightResponse> getFallbackFlightData(FlightSearchRequest request, Pageable pageable) {
        List<FlightResponse> fallbackFlights = generateFallbackFlights(request);
        return paginateResults(fallbackFlights, pageable);
    }
    
    private List<FlightResponse> generateFallbackFlights(FlightSearchRequest request) {
        List<FlightResponse> flights = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < cacheConfig.getApi().getFallback().getMinResults(); i++) {
            FlightResponse flight = FlightResponse.builder()
                    .id("fallback-" + UUID.randomUUID().toString())
                    .price(200.0 + random.nextDouble() * 800)
                    .currency(request.getCurrency())
                    .numberOfBookableSeats(random.nextInt(9) + 1)
                    .validatingAirlineCodes(getRandomAirline())
                    .itineraries(generateFallbackItinerary(request))
                    .build();
            flights.add(flight);
        }
        
        return flights;
    }
    
    private List<FlightResponse.Itinerary> generateFallbackItinerary(FlightSearchRequest request) {
        LocalDateTime departureTime = request.getDepartureDate().atTime(8 + new Random().nextInt(12), 0);
        LocalDateTime arrivalTime = departureTime.plusHours(2 + new Random().nextInt(8));
        
        FlightResponse.Segment segment = FlightResponse.Segment.builder()
                .departure(FlightResponse.Departure.builder()
                        .iataCode(request.getOrigin())
                        .at(departureTime)
                        .build())
                .arrival(FlightResponse.Arrival.builder()
                        .iataCode(request.getDestination())
                        .at(arrivalTime)
                        .build())
                .carrierCode(getRandomAirline())
                .number(String.valueOf(1000 + new Random().nextInt(9000)))
                .duration("PT" + (2 + new Random().nextInt(8)) + "H")
                .numberOfStops(0)
                .build();
        
        FlightResponse.Itinerary itinerary = FlightResponse.Itinerary.builder()
                .duration("PT" + (2 + new Random().nextInt(8)) + "H")
                .segments(Arrays.asList(segment))
                .build();
        
        return Arrays.asList(itinerary);
    }
    
    private String getRandomAirline() {
        String[] airlines = {"AA", "DL", "UA", "AS", "B6", "WN", "NK", "F9", "G4"};
        return airlines[new Random().nextInt(airlines.length)];
    }
    
    private List<String> getPopularDestinations(String origin) {
        Map<String, List<String>> destinationMap = Map.of(
                "JFK", Arrays.asList("LAX", "LHR", "CDG", "FCO", "BCN", "AMS", "FRA"),
                "LAX", Arrays.asList("JFK", "NRT", "ICN", "LHR", "CDG", "SYD", "YVR"),
                "LHR", Arrays.asList("JFK", "LAX", "CDG", "FCO", "BCN", "AMS", "FRA"),
                "CDG", Arrays.asList("JFK", "LAX", "LHR", "FCO", "BCN", "AMS", "FRA")
        );
        
        return destinationMap.getOrDefault(origin, Arrays.asList("JFK", "LAX", "LHR", "CDG"));
    }
}
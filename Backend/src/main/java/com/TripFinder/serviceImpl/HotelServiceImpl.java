package com.TripFinder.serviceImpl;

import com.TripFinder.config.CacheConfig;
import com.TripFinder.dto.HotelSearchRequest;
import com.TripFinder.dto.HotelResponse;
import com.TripFinder.entity.Hotel;
import com.TripFinder.repository.HotelRepo;
import com.TripFinder.service.HotelService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {
    
    private static final Logger logger = LoggerFactory.getLogger(HotelServiceImpl.class);
    
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
    
    @Autowired
    private HotelRepo hotelRepo;
    
    // Rate limiting
    private final Map<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final long windowSizeMs = 1000; // 1 second window
    
    @Override
    public List<Hotel> getAllHotels() {
        return hotelRepo.findAll();
    }
    
    @Override
    @Cacheable(value = "hotel-offers", key = "#searchRequest.hashCode() + '_' + #pageable.pageNumber")
    @Retry(name = "amadeus-api")
    public Page<HotelResponse> searchHotels(HotelSearchRequest searchRequest, Pageable pageable) {
        logger.info("Searching hotels in {} from {} to {}", 
                searchRequest.getCityCode(), searchRequest.getCheckInDate(), searchRequest.getCheckOutDate());
        
        try {
            // Check rate limits
            if (!checkRateLimit()) {
                logger.warn("Rate limit exceeded, using fallback data");
                return getFallbackHotelData(searchRequest, pageable);
            }
            
            // Get access token
            String accessToken = getAccessToken();
            
            // Step 1: Search for hotels by location
            List<String> hotelIds = searchHotelsByLocation(accessToken, searchRequest);
            
            if (hotelIds.isEmpty()) {
                logger.warn("No hotels found for location: {}", searchRequest.getCityCode());
                return getFallbackHotelData(searchRequest, pageable);
            }
            
            // Step 2: Get hotel offers for found hotels
            List<HotelResponse> hotels = getHotelOffers(accessToken, hotelIds, searchRequest);
            
            // Apply client-side filtering and sorting
            hotels = filterAndSortHotels(hotels, searchRequest);
            
            // Handle pagination
            return paginateResults(hotels, pageable);
            
        } catch (Exception e) {
            logger.error("Error searching hotels: {}", e.getMessage(), e);
            
            // Return fallback data if enabled
            if (cacheConfig.getApi().getFallback().isEnabled()) {
                logger.info("Returning fallback hotel data");
                return getFallbackHotelData(searchRequest, pageable);
            }
            
            throw new RuntimeException("Hotel search failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public HotelResponse getHotelDetails(String hotelId, String offerId) {
        logger.info("Getting hotel details for hotel ID: {} and offer ID: {}", hotelId, offerId);
        
        try {
            String accessToken = getAccessToken();
            String detailsUrl = baseUrl + "/v3/shopping/hotel-offers/" + offerId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    detailsUrl, HttpMethod.GET, requestEntity, JsonNode.class
            );
            
            return parseHotelOffer(response.getBody());
            
        } catch (Exception e) {
            logger.error("Error getting hotel details: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get hotel details: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<HotelResponse> getHotelsByLocation(String cityCode, String checkIn, String checkOut, Integer adults) {
        HotelSearchRequest request = HotelSearchRequest.builder()
                .cityCode(cityCode)
                .checkInDate(LocalDate.parse(checkIn))
                .checkOutDate(LocalDate.parse(checkOut))
                .adults(adults)
                .build();
                
        Page<HotelResponse> result = searchHotels(request, org.springframework.data.domain.PageRequest.of(0, 20));
        return result.getContent();
    }
    
    @Override
    public List<String> getPopularDestinations() {
        return Arrays.asList(
                "NYC", "LAX", "LHR", "CDG", "NRT", "BCN",
                "ROM", "AMS", "BER", "MAD", "LIS", "DUB"
        );
    }
    
    @Override
    @CacheEvict(value = {"hotel-offers", "amadeus-tokens"}, allEntries = true)
    public void clearHotelCache() {
        logger.info("Clearing hotel cache");
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
    
    private List<String> searchHotelsByLocation(String accessToken, HotelSearchRequest request) {
        String searchUrl = buildHotelSearchUrl(request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    searchUrl, HttpMethod.GET, requestEntity, JsonNode.class
            );
            
            List<String> hotelIds = new ArrayList<>();
            JsonNode data = response.getBody().path("data");
            
            for (JsonNode hotel : data) {
                String hotelId = hotel.path("hotelId").asText();
                if (!hotelId.isEmpty()) {
                    hotelIds.add(hotelId);
                }
            }
            
            return hotelIds.subList(0, Math.min(hotelIds.size(), 50)); // Limit to 50 hotels
            
        } catch (Exception e) {
            logger.error("Error searching hotels by location: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    private List<HotelResponse> getHotelOffers(String accessToken, List<String> hotelIds, HotelSearchRequest request) {
        if (hotelIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        String offersUrl = buildHotelOffersUrl(hotelIds, request);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    offersUrl, HttpMethod.GET, requestEntity, JsonNode.class
            );
            
            return parseHotelOffers(response.getBody());
            
        } catch (Exception e) {
            logger.error("Error getting hotel offers: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    private String buildHotelSearchUrl(HotelSearchRequest request) {
        StringBuilder url = new StringBuilder(baseUrl + "/v1/reference-data/locations/hotels/by-city");
        url.append("?cityCode=").append(request.getCityCode());
        
        if (request.getRadius() != null) {
            url.append("&radius=").append(request.getRadius());
        }
        
        return url.toString();
    }
    
    private String buildHotelOffersUrl(List<String> hotelIds, HotelSearchRequest request) {
        StringBuilder url = new StringBuilder(baseUrl + "/v3/shopping/hotel-offers");
        url.append("?hotelIds=").append(String.join(",", hotelIds));
        url.append("&checkInDate=").append(request.getCheckInDate());
        url.append("&checkOutDate=").append(request.getCheckOutDate());
        url.append("&adults=").append(request.getAdults());
        
        if (request.getRooms() > 1) {
            url.append("&rooms=").append(request.getRooms());
        }
        
        url.append("&currency=").append(request.getCurrency());
        url.append("&lang=").append(request.getLang());
        url.append("&bestRateOnly=").append(request.getBestRateOnly());
        
        return url.toString();
    }
    
    private List<HotelResponse> parseHotelOffers(JsonNode responseBody) {
        List<HotelResponse> hotels = new ArrayList<>();
        
        if (responseBody != null && responseBody.has("data")) {
            JsonNode data = responseBody.get("data");
            
            for (JsonNode hotelOffer : data) {
                try {
                    HotelResponse hotel = parseHotelOffer(hotelOffer);
                    if (hotel != null) {
                        hotels.add(hotel);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse hotel offer: {}", e.getMessage());
                }
            }
        }
        
        return hotels;
    }
    
    private HotelResponse parseHotelOffer(JsonNode hotelOffer) {
        if (hotelOffer == null) return null;
        
        try {
            JsonNode hotel = hotelOffer.path("hotel");
            
            HotelResponse.HotelResponseBuilder builder = HotelResponse.builder()
                    .hotelId(hotel.path("hotelId").asText())
                    .chainCode(hotel.path("chainCode").asText())
                    .dupeId(hotel.path("dupeId").asText())
                    .name(hotel.path("name").asText())
                    .rating(hotel.path("rating").asInt())
                    .description(parseDescription(hotel))
                    .amenities(parseAmenities(hotel))
                    .contact(parseContact(hotel))
                    .address(parseAddress(hotel))
                    .geoCode(parseGeoCode(hotel));
            
            // Parse offers
            List<HotelResponse.RoomOffer> offers = new ArrayList<>();
            JsonNode offersNode = hotelOffer.path("offers");
            
            for (JsonNode offerNode : offersNode) {
                HotelResponse.RoomOffer offer = parseRoomOffer(offerNode);
                if (offer != null) {
                    offers.add(offer);
                }
            }
            
            builder.offers(offers);
            
            return builder.build();
            
        } catch (Exception e) {
            logger.warn("Failed to parse hotel offer: {}", e.getMessage());
            return null;
        }
    }
    
    private String parseDescription(JsonNode hotel) {
        JsonNode description = hotel.path("description").path("text");
        return description.asText("");
    }
    
    private List<String> parseAmenities(JsonNode hotel) {
        List<String> amenities = new ArrayList<>();
        JsonNode amenitiesNode = hotel.path("amenities");
        
        for (JsonNode amenity : amenitiesNode) {
            amenities.add(amenity.asText());
        }
        
        return amenities;
    }
    
    private HotelResponse.Contact parseContact(JsonNode hotel) {
        JsonNode contact = hotel.path("contact");
        return HotelResponse.Contact.builder()
                .phone(contact.path("phone").asText())
                .fax(contact.path("fax").asText())
                .email(contact.path("email").asText())
                .build();
    }
    
    private HotelResponse.Address parseAddress(JsonNode hotel) {
        JsonNode address = hotel.path("address");
        List<String> lines = new ArrayList<>();
        JsonNode linesNode = address.path("lines");
        
        for (JsonNode line : linesNode) {
            lines.add(line.asText());
        }
        
        return HotelResponse.Address.builder()
                .lines(lines)
                .postalCode(address.path("postalCode").asText())
                .cityName(address.path("cityName").asText())
                .countryCode(address.path("countryCode").asText())
                .stateCode(address.path("stateCode").asText())
                .build();
    }
    
    private HotelResponse.GeoCode parseGeoCode(JsonNode hotel) {
        JsonNode geoCode = hotel.path("geoCode");
        return HotelResponse.GeoCode.builder()
                .latitude(geoCode.path("latitude").asDouble())
                .longitude(geoCode.path("longitude").asDouble())
                .build();
    }
    
    private HotelResponse.RoomOffer parseRoomOffer(JsonNode offerNode) {
        try {
            return HotelResponse.RoomOffer.builder()
                    .id(offerNode.path("id").asText())
                    .checkInDate(LocalDate.parse(offerNode.path("checkInDate").asText()))
                    .checkOutDate(LocalDate.parse(offerNode.path("checkOutDate").asText()))
                    .rateCode(offerNode.path("rateCode").asText())
                    .price(parsePrice(offerNode.path("price")))
                    .room(parseRoom(offerNode.path("room")))
                    .guests(parseGuests(offerNode.path("guests")))
                    .build();
        } catch (Exception e) {
            logger.warn("Failed to parse room offer: {}", e.getMessage());
            return null;
        }
    }
    
    private HotelResponse.Price parsePrice(JsonNode priceNode) {
        return HotelResponse.Price.builder()
                .currency(priceNode.path("currency").asText())
                .base(priceNode.path("base").asDouble())
                .total(priceNode.path("total").asDouble())
                .build();
    }
    
    private HotelResponse.Room parseRoom(JsonNode roomNode) {
        return HotelResponse.Room.builder()
                .type(roomNode.path("type").asText())
                .typeEstimated(parseTypeEstimated(roomNode.path("typeEstimated")))
                .description(parseRoomDescription(roomNode.path("description")))
                .build();
    }
    
    private HotelResponse.TypeEstimated parseTypeEstimated(JsonNode typeNode) {
        return HotelResponse.TypeEstimated.builder()
                .category(typeNode.path("category").asText())
                .beds(typeNode.path("beds").asInt())
                .bedType(typeNode.path("bedType").asText())
                .build();
    }
    
    private HotelResponse.Description parseRoomDescription(JsonNode descNode) {
        return HotelResponse.Description.builder()
                .text(descNode.path("text").asText())
                .lang(descNode.path("lang").asText())
                .build();
    }
    
    private HotelResponse.Guests parseGuests(JsonNode guestsNode) {
        return HotelResponse.Guests.builder()
                .adults(guestsNode.path("adults").asInt())
                .build();
    }
    
    private List<HotelResponse> filterAndSortHotels(List<HotelResponse> hotels, HotelSearchRequest request) {
        return hotels.stream()
                .filter(hotel -> applyFilters(hotel, request))
                .sorted(getComparator(request))
                .collect(Collectors.toList());
    }
    
    private boolean applyFilters(HotelResponse hotel, HotelSearchRequest request) {
        // Apply rating filter
        if (request.getRatings() != null && !request.getRatings().isEmpty()) {
            if (!request.getRatings().contains(hotel.getRating())) {
                return false;
            }
        }
        
        // Apply price filter
        if (hotel.getOffers() != null && !hotel.getOffers().isEmpty()) {
            double minPrice = hotel.getOffers().stream()
                    .mapToDouble(offer -> offer.getPrice().getTotal())
                    .min().orElse(Double.MAX_VALUE);
            
            if (request.getPriceMin() != null && minPrice < request.getPriceMin()) {
                return false;
            }
            
            if (request.getPriceMax() != null && minPrice > request.getPriceMax()) {
                return false;
            }
        }
        
        // Apply amenity filter
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            if (hotel.getAmenities() == null) {
                return false;
            }
            
            boolean hasRequiredAmenities = request.getAmenities().stream()
                    .anyMatch(amenity -> hotel.getAmenities().contains(amenity));
            
            if (!hasRequiredAmenities) {
                return false;
            }
        }
        
        return true;
    }
    
    private Comparator<HotelResponse> getComparator(HotelSearchRequest request) {
        Comparator<HotelResponse> comparator;
        
        switch (request.getSortBy().toLowerCase()) {
            case "price":
                comparator = Comparator.comparing(hotel -> {
                    return hotel.getOffers() != null && !hotel.getOffers().isEmpty()
                            ? hotel.getOffers().get(0).getPrice().getTotal()
                            : Double.MAX_VALUE;
                });
                break;
            case "rating":
                comparator = Comparator.comparing(HotelResponse::getRating, Comparator.reverseOrder());
                break;
            case "distance":
                comparator = Comparator.comparing(HotelResponse::getDistance, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            default:
                comparator = Comparator.comparing(hotel -> {
                    return hotel.getOffers() != null && !hotel.getOffers().isEmpty()
                            ? hotel.getOffers().get(0).getPrice().getTotal()
                            : Double.MAX_VALUE;
                });
        }
        
        if ("desc".equalsIgnoreCase(request.getSortOrder())) {
            comparator = comparator.reversed();
        }
        
        return comparator;
    }
    
    private Page<HotelResponse> paginateResults(List<HotelResponse> hotels, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), hotels.size());
        
        List<HotelResponse> pageContent = hotels.subList(start, end);
        return new PageImpl<>(pageContent, pageable, hotels.size());
    }
    
    private boolean checkRateLimit() {
        String key = "amadeus-api";
        AtomicLong counter = requestCounts.computeIfAbsent(key, k -> new AtomicLong(0));
        
        // Simple sliding window rate limiting
        if (counter.get() >= cacheConfig.getRateLimit().getAmadeus().getRequestsPerSecond()) {
            return false;
        }
        
        counter.incrementAndGet();
        
        // Reset counter every second (simplified implementation)
        if (System.currentTimeMillis() % 1000 < 100) {
            counter.set(0);
        }
        
        return true;
    }
    
    private Page<HotelResponse> getFallbackHotelData(HotelSearchRequest request, Pageable pageable) {
        List<HotelResponse> fallbackHotels = generateFallbackHotels(request);
        return paginateResults(fallbackHotels, pageable);
    }
    
    private List<HotelResponse> generateFallbackHotels(HotelSearchRequest request) {
        List<HotelResponse> hotels = new ArrayList<>();
        Random random = new Random();
        String[] hotelNames = {"Grand Hotel", "City Center Hotel", "Business Hotel", "Comfort Inn", "Luxury Suite Hotel"};
        
        for (int i = 0; i < cacheConfig.getApi().getFallback().getMinResults(); i++) {
            HotelResponse hotel = HotelResponse.builder()
                    .hotelId("fallback-" + UUID.randomUUID().toString())
                    .name(hotelNames[random.nextInt(hotelNames.length)] + " " + (i + 1))
                    .rating(3 + random.nextInt(3)) // 3-5 stars
                    .description("A comfortable hotel in " + request.getCityCode())
                    .amenities(Arrays.asList("WiFi", "Parking", "Restaurant"))
                    .offers(generateFallbackOffers(request, random))
                    .build();
            hotels.add(hotel);
        }
        
        return hotels;
    }
    
    private List<HotelResponse.RoomOffer> generateFallbackOffers(HotelSearchRequest request, Random random) {
        HotelResponse.RoomOffer offer = HotelResponse.RoomOffer.builder()
                .id("offer-" + UUID.randomUUID().toString())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .price(HotelResponse.Price.builder()
                        .currency(request.getCurrency())
                        .total(80.0 + random.nextDouble() * 320) // $80-400 per night
                        .build())
                .room(HotelResponse.Room.builder()
                        .type("STANDARD")
                        .typeEstimated(HotelResponse.TypeEstimated.builder()
                                .category("STANDARD_ROOM")
                                .beds(1 + random.nextInt(2))
                                .bedType(random.nextBoolean() ? "KING" : "DOUBLE")
                                .build())
                        .build())
                .guests(HotelResponse.Guests.builder()
                        .adults(request.getAdults())
                        .build())
                .build();
        
        return Arrays.asList(offer);
    }
}

package com.TripFinder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for flight search response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightResponse {
    
    private String id;
    private Double price;
    private String currency;
    private Integer numberOfBookableSeats;
    private String validatingAirlineCodes;
    private List<Itinerary> itineraries;
    private PricingOptions pricingOptions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Itinerary {
        private String duration;
        private List<Segment> segments;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Segment {
        private Departure departure;
        private Arrival arrival;
        private String carrierCode;
        private String number;
        private Aircraft aircraft;
        private Operating operating;
        private String duration;
        private String id;
        private Integer numberOfStops;
        private String cabin;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Departure {
        private String iataCode;
        private String terminal;
        private LocalDateTime at;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Arrival {
        private String iataCode;
        private String terminal;
        private LocalDateTime at;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Aircraft {
        private String code;
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Operating {
        private String carrierCode;
        private String number;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingOptions {
        private List<String> fareType;
        private Boolean includedCheckedBagsOnly;
    }
}
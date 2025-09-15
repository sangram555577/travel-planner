package com.TripFinder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for hotel search response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {
    
    private String hotelId;
    private String chainCode;
    private String dupeId;
    private String name;
    private Integer rating;
    private String description;
    private List<String> amenities;
    private Contact contact;
    private Address address;
    private GeoCode geoCode;
    private Double distance;
    private String distanceUnit;
    private String lastUpdate;
    private List<RoomOffer> offers;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Contact {
        private String phone;
        private String fax;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        private List<String> lines;
        private String postalCode;
        private String cityName;
        private String countryCode;
        private String stateCode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeoCode {
        private Double latitude;
        private Double longitude;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomOffer {
        private String id;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private String rateCode;
        private RatePlan ratePlan;
        private Room room;
        private Guests guests;
        private Price price;
        private Policies policies;
        private String self; // Link to offer details
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RatePlan {
        private String category;
        private Boolean paymentPolicy;
        private String cancellationPolicy;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Room {
        private String type;
        private TypeEstimated typeEstimated;
        private Description description;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TypeEstimated {
        private String category;
        private Integer beds;
        private String bedType;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Description {
        private String text;
        private String lang;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Guests {
        private Integer adults;
        private Integer childAges;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Price {
        private String currency;
        private Double base;
        private Double total;
        private List<Variation> variations;
        private Changes changes;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Variation {
        private String startDate;
        private String endDate;
        private String currency;
        private Double sellingTotal;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Changes {
        private List<PolicyDetail> cancellation;
        private List<PolicyDetail> prepay;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PolicyDetail {
        private String deadline;
        private String amount;
        private String percent;
        private String type;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Policies {
        private String holdTime;
        private String guarantee;
        private String deposit;
        private String prepay;
        private String checkInOut;
        private String cancellation;
    }
}
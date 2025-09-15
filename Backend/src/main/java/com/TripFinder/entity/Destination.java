package com.TripFinder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a travel destination.
 * This entity maps to the 'destinations' table in the database.
 */
@Entity
@Table(name = "destinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(name = "back_image", columnDefinition = "TEXT")
    private String backImage;

    @NotBlank(message = "Location cannot be blank")
    @Size(max = 255, message = "Location name must not exceed 255 characters")
    @Column(nullable = false)
    private String location;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 255, message = "City name must not exceed 255 characters")
    @Column(nullable = false)
    private String city;

    @Lob
    @NotBlank(message = "Description cannot be blank")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Lob
    @Column(name = "overview_image", columnDefinition = "TEXT")
    private String overviewImage;

    @Lob
    @Column(name = "popular_spots", columnDefinition = "TEXT")
    private String popularSpots;
}
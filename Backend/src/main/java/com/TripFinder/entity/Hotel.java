package com.TripFinder.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Represents a Hotel available for booking.
 * This entity maps to the 'hotels' table in the database.
 */
@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Hotel name cannot be blank")
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "Location cannot be blank")
    @Size(max = 255)
    @Column(nullable = false)
    private String location;

    @Positive(message = "Price must be a positive value")
    @Column(nullable = false)
    private double price;

    @Positive(message = "Rating must be a positive value")
    @Column(nullable = false)
    private double rating;

    @URL(message = "A valid image URL is required")
    @Column(name = "imageURL", nullable = false, columnDefinition = "TEXT") // Matching the original casing
    private String imageURL;
}
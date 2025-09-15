package com.TripFinder.controller;

import com.TripFinder.entity.Hotel;
import com.TripFinder.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for public hotel-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/hotels")
@CrossOrigin(origins = "http://localhost:5173")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    /**
     * Get all hotels.
     *
     * @return A ResponseEntity containing a list of all hotels.
     */
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        List<Hotel> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }
}
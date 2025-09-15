package com.TripFinder.service;

import com.TripFinder.entity.Hotel;
import java.util.List;

/**
 * Service interface for managing hotel-related business logic.
 */
public interface HotelService {

    /**
     * Retrieves all hotels.
     *
     * @return A list of all Hotel entities.
     */
    List<Hotel> getAllHotels();
}
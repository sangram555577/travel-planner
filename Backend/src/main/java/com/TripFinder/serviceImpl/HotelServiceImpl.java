package com.TripFinder.serviceImpl;

import com.TripFinder.entity.Hotel;
import com.TripFinder.repository.HotelRepo;
import com.TripFinder.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the HotelService for handling hotel data.
 */
@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepo hotelRepo;

    @Override
    public List<Hotel> getAllHotels() {
        return hotelRepo.findAll();
    }
}
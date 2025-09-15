package com.TripFinder.serviceImpl;

import com.TripFinder.dto.ItineraryDto;
import com.TripFinder.entity.Itinerary;
import com.TripFinder.entity.User;
import com.TripFinder.repository.ItineraryRepo;
import com.TripFinder.repository.UserRepo;
import com.TripFinder.service.ItineraryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    @Autowired
    private ItineraryRepo itineraryRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public Itinerary saveItinerary(ItineraryDto itineraryDto) {
        validateDto(itineraryDto);
        User user = userRepo.findById(itineraryDto.userId())
                .orElseThrow(() -> new RuntimeException("User not found, cannot save itinerary."));

        Itinerary itinerary = new Itinerary();
        BeanUtils.copyProperties(itineraryDto, itinerary);
        itinerary.setUser(user);

        return itineraryRepo.save(itinerary);
    }

    @Override
    public List<Itinerary> getItinerariesByUserId(int userId) {
        return itineraryRepo.findByUserId(userId);
    }

    @Override
    public Itinerary updateItinerary(Long id, ItineraryDto itineraryDto) {
        validateDto(itineraryDto);
        Itinerary existingItinerary = itineraryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found with ID: " + id));

        User user = userRepo.findById(itineraryDto.userId())
                .orElseThrow(() -> new RuntimeException("User not found, cannot update itinerary."));

        BeanUtils.copyProperties(itineraryDto, existingItinerary);
        existingItinerary.setId(id); // Ensure the ID is not changed
        existingItinerary.setUser(user);

        return itineraryRepo.save(existingItinerary);
    }

    @Override
    public void deleteItinerary(Long id) {
        if (!itineraryRepo.existsById(id)) {
            throw new RuntimeException("Itinerary not found with ID: " + id);
        }
        itineraryRepo.deleteById(id);
    }

    private void validateDto(ItineraryDto dto) {
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new RuntimeException("End date cannot be before start date.");
        }
    }
}
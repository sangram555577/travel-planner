package com.TripFinder.serviceImpl;

import com.TripFinder.entity.Destination;
import com.TripFinder.repository.DestinationRepo;
import com.TripFinder.service.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the DestinationService for handling destination data.
 */
@Service
public class DestinationServiceImpl implements DestinationService {

    @Autowired
    private DestinationRepo destinationRepo;

    @Override
    public List<Destination> getAllDestinations() {
        return destinationRepo.findAll();
    }

    @Override
    public Optional<Destination> getDestinationById(int id) {
        return destinationRepo.findById(id);
    }
}
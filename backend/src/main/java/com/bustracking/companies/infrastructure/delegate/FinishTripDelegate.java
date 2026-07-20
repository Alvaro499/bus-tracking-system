package com.bustracking.companies.infrastructure.delegate;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.FinishTrip;
import com.bustracking.tracking.domain.model.TripFinishView;

@Component
public class FinishTripDelegate implements FinishTrip {

    private final TripRepository tripRepository;

    public FinishTripDelegate(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public TripFinishView execute(UUID tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) {
            throw new NotFoundException(
                ErrorCode.TRIP_NOT_FOUND,
                "Trip not found",
                "Trip with ID " + tripId + " does not exist"
            );
        }

        Trip trip = optionalTrip.get();
       
        trip.complete();
        tripRepository.save(trip);

        return new TripFinishView(
            trip.getId(),
            trip.getStatus().name(),
            trip.getActualStartTime(),
            trip.getActualEndTime(),
            trip.getDelayMinutes() != null ? trip.getDelayMinutes() : 0
        );
    }
}
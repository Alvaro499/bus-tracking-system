package com.bustracking.companies.infrastructure.delegate;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bustracking.companies.domain.enums.TripStatus;
import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.shared.exception.BusinessRuleException;
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
        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Trip is not in progress",
                "Only trips in IN_PROGRESS can be finished"
            );
        }

        trip.complete(); // actualiza actualEndTime y status a COMPLETED
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
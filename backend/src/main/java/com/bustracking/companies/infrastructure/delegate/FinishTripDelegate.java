package com.bustracking.companies.infrastructure.delegate;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bustracking.companies.domain.model.Schedule;
import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.repository.ScheduleRepository;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.FinishTrip;
import com.bustracking.tracking.domain.model.TripFinishView;

@Component
public class FinishTripDelegate implements FinishTrip {

    // TODO: Log Global A Futuro
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FinishTripDelegate.class);

    private final TripRepository tripRepository;
    private final ScheduleRepository scheduleRepository;

    public FinishTripDelegate(TripRepository tripRepository, ScheduleRepository scheduleRepository) {
        this.tripRepository = tripRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public TripFinishView execute(UUID tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isEmpty()) {
            throw new NotFoundException(
                    ErrorCode.TRIP_NOT_FOUND,
                    "Trip not found",
                    "Trip with ID " + tripId + " does not exist");
        }
        Trip trip = optionalTrip.get();

        Optional<Schedule> optionalSchedule = scheduleRepository.findById(trip.getScheduleId());
        Schedule schedule = null;
        if (optionalSchedule.isPresent()) {
            schedule = optionalSchedule.get();
        }

        trip.complete(schedule);

        tripRepository.save(trip);

        return new TripFinishView(
                trip.getId(),
                trip.getStatus().name(),
                trip.getActualStartTime(),
                trip.getActualEndTime(),
                trip.getDelayMinutes()
            );
    }

}
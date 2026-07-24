package com.bustracking.companies.infrastructure.delegate;

import java.time.Duration;
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

    //TODO: Log Global A Futuro
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
                "Trip with ID " + tripId + " does not exist"
            );
        }

        Trip trip = optionalTrip.get();       
        trip.complete();

        int delayInMinutes = calculateDelay(trip);
        trip.registerDelay(delayInMinutes);

        tripRepository.save(trip);

        return new TripFinishView(
            trip.getId(),
            trip.getStatus().name(),
            trip.getActualStartTime(),
            trip.getActualEndTime(),
            delayInMinutes
        );
    }

    private int calculateDelay(Trip trip) {
        try {

            Optional<Schedule> optionalSchedule = scheduleRepository.findById(trip.getScheduleId());

            if(optionalSchedule.isEmpty()){
                throw new NotFoundException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                     "Schedule not found",
                     "Schedule not found for trip " + trip.getId()
                    );
            }
            Schedule schedule = optionalSchedule.get();
            return Math.max(0, (int) Duration.between(
                    schedule.getDepartureTime(),
                    trip.getActualEndTime()).toMinutes());

        } catch (NotFoundException e) {

            // Non‑critical: the trip can still be finished without this value
            log.warn("Schedule not found for trip {}, delay set to 0", trip.getId());
            return 0;
        }
    }
}
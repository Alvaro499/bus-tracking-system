package com.bustracking.companies.infrastructure.delegate;

import com.bustracking.companies.domain.dto.TripStopDetailProjection;
import com.bustracking.companies.domain.enums.TripStatus;
import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.model.TripStop;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.companies.domain.repository.TripStopRepository;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.ConfirmStop;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConfirmStopDelegate implements ConfirmStop {

        private final TripRepository tripRepository;
        private final TripStopRepository tripStopRepository;

        public ConfirmStopDelegate(TripRepository tripRepository, TripStopRepository tripStopRepository) {
                this.tripRepository = tripRepository;
                this.tripStopRepository = tripStopRepository;
        }

        @Override
        public void execute(UUID tripId, UUID stopId) {

                // We find our trip
                Optional<Trip> optionalTrip = tripRepository.findById(tripId);
                if (optionalTrip.isEmpty()) {
                        throw new NotFoundException(
                                        ErrorCode.TRIP_NOT_FOUND,
                                        "Trip not found",
                                        "Trip with ID " + tripId + " does not exist");
                }
                Trip trip = optionalTrip.get();

                // We validate that our trip is in progress
                if (trip.getStatus() != TripStatus.IN_PROGRESS) {
                        throw new BusinessRuleException(
                                        ErrorCode.INVALID_STATE,
                                        "Trip is not in progress",
                                        "Only trips with status IN_PROGRESS can have stops confirmed");
                }

                // We find all stops for this trip
                List<TripStopDetailProjection> stops = tripRepository.findStopsByTripId(tripId);
                if (stops.isEmpty()) {
                        throw new BusinessRuleException(
                                        ErrorCode.INVALID_STATE,
                                        "Trip has no stops",
                                        "Cannot confirm a stop on a trip with no stops");
                }

                // We find the first uncompleted stop
                TripStopDetailProjection firstUncompleted = stops.stream()
                                .filter(s -> s.completedAt() == null)
                                .findFirst()
                                .orElseThrow(() -> new BusinessRuleException(
                                                ErrorCode.INVALID_STATE,
                                                "All stops already completed",
                                                "Cannot confirm a stop because all stops are already completed"));

                // We verify that the stopId being confirmed is the first uncompleted stop
                if (!firstUncompleted.routeStopId().equals(stopId)) {
                        throw new BusinessRuleException(
                                        ErrorCode.INVALID_STATE,
                                        "Stop out of order",
                                        "Only the first uncompleted stop can be confirmed");
                }

                // 6. Actualizar el TripStop
                Optional<TripStop> optionalTripStop = tripStopRepository.findByTripIdAndRouteStopId(tripId, stopId);
                if (optionalTripStop.isEmpty()) {
                        throw new NotFoundException(
                                        ErrorCode.STOP_NOT_FOUND,
                                        "Stop not found for this trip",
                                        "No TripStop found for tripId " + tripId + " and routeStopId " + stopId);
                }
                TripStop tripStop = optionalTripStop.get();

                tripStop.markCompleted();
                tripStopRepository.save(tripStop);
        }
}

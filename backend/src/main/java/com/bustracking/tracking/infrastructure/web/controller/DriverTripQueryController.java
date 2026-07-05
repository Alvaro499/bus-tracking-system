package com.bustracking.tracking.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bustracking.tracking.application.usecase.GetTodayPlannedTripsUseCase;
import com.bustracking.tracking.application.usecase.GetTripDetailUseCase;
import com.bustracking.tracking.domain.model.TripDetailView;

import com.bustracking.tracking.domain.model.TripView;
import com.bustracking.tracking.infrastructure.mappers.TripDetailMapper;
import com.bustracking.tracking.infrastructure.web.dto.response.TripDetailResponse;
import com.bustracking.tracking.infrastructure.web.dto.response.TripResponse;

@RestController
@RequestMapping("/tracking/trips")
public class DriverTripQueryController {

    //Use Cases
    private final GetTodayPlannedTripsUseCase getTodayPlannedTripsUseCase;
    private final GetTripDetailUseCase getTripDetailUseCase;

    //Mappers
    private final TripDetailMapper tripDetailMapper;

    public DriverTripQueryController(
        GetTodayPlannedTripsUseCase getTodayPlannedTripsUseCase,
        GetTripDetailUseCase getTripDetailUseCase,
        TripDetailMapper tripDetailMapper) {
        this.getTodayPlannedTripsUseCase = getTodayPlannedTripsUseCase;
        this.getTripDetailUseCase = getTripDetailUseCase;
        this.tripDetailMapper = tripDetailMapper;
    }

    @GetMapping("/today")
    public ResponseEntity<List<TripResponse>> getTodayPlannedTripsForDriver() {

        UUID busId = getCurrentBusId();
        List<TripView> trips = getTodayPlannedTripsUseCase.execute(busId);

        List<TripResponse> response = trips.stream()
                .map(trip -> new TripResponse(
                        trip.getId(),
                        trip.getRouteName(),
                        trip.getOrigin(),
                        trip.getDestination(),
                        trip.getDepartureTime(),
                        trip.getStatus()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tripId}/detail")
    public ResponseEntity<TripDetailResponse> getTripDetail(@PathVariable UUID tripId) {
        
        UUID busId = getCurrentBusId();
        TripDetailView detailView = getTripDetailUseCase.execute(busId, tripId);
        TripDetailResponse response = tripDetailMapper.toResponse(detailView);
        return ResponseEntity.ok(response);
    }

    private UUID getCurrentBusId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
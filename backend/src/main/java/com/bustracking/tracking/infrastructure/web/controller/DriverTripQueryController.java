package com.bustracking.tracking.infrastructure.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bustracking.tracking.application.usecase.GetTodayPlannedTripsUseCase;
import com.bustracking.tracking.application.usecase.GetTripDetailUseCase;
import com.bustracking.tracking.domain.model.RouteStopView;
import com.bustracking.tracking.domain.model.StopView;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.domain.model.TripStopDetailView;
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
        // TODO: extraer busId del JWT cuando HU-18 esté implementada

        UUID busId = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
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
        UUID busId = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
        TripDetailView detailView = getTripDetailUseCase.execute(busId, tripId);
        TripDetailResponse response = tripDetailMapper.toResponse(detailView);
        return ResponseEntity.ok(response);
    }
}
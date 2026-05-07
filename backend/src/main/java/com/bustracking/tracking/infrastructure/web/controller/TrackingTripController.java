package com.bustracking.tracking.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bustracking.tracking.application.usecase.GetTodayTripsUseCase;
import com.bustracking.tracking.domain.model.TripView;
import com.bustracking.tracking.infrastructure.web.dto.response.TripResponse;

@RestController
@RequestMapping("/tracking/trips")
public class TrackingTripController {

    private final GetTodayTripsUseCase getTodayTripsUseCase;

    public TrackingTripController(GetTodayTripsUseCase getTodayTripsUseCase) {
        this.getTodayTripsUseCase = getTodayTripsUseCase;
    }

        @GetMapping("/today")
    public ResponseEntity<List<TripResponse>> getTodayTrips() {
        // TODO: extraer busId del JWT cuando HU-18 esté implementada
        UUID busId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        List<TripView> trips = getTodayTripsUseCase.execute(busId);

        List<TripResponse> response = trips.stream()
            .map(trip -> new TripResponse(
                trip.getId(),
                trip.getScheduleId(),
                trip.getBusId(),
                trip.getTripDate(),
                trip.getStatus(),
                trip.getActualStartTime(),
                trip.getActualEndTime()
            ))
            .toList();

        return ResponseEntity.ok(response);
    }
}

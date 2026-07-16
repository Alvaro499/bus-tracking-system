package com.bustracking.tracking.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bustracking.tracking.application.usecase.ConfirmStopUseCase;
import com.bustracking.tracking.application.usecase.FinishTripUseCase;
import com.bustracking.tracking.application.usecase.StartTripUseCase;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.infrastructure.mappers.TripDetailMapper;
import com.bustracking.tracking.infrastructure.web.dto.response.TripDetailResponse;

@RestController
@RequestMapping("/tracking/trips")
public class DriverTripCommandController {

    private final StartTripUseCase startTripUseCase;
    private final ConfirmStopUseCase confirmStopUseCase;

    private final FinishTripUseCase finishTripUseCase;

    // Mappers
    private final TripDetailMapper tripDetailMapper;

    public DriverTripCommandController(StartTripUseCase startTripUseCase, ConfirmStopUseCase confirmStopUseCase,
            FinishTripUseCase finishTripUseCase,TripDetailMapper tripDetailMapper) {
        this.startTripUseCase = startTripUseCase;
        this.confirmStopUseCase = confirmStopUseCase;
        this.tripDetailMapper = tripDetailMapper;
        this.finishTripUseCase = finishTripUseCase;
    }

    @PostMapping("/{tripId}/start")
    public ResponseEntity<Void> startTrip(@PathVariable UUID tripId) {

        UUID busId = getCurrentBusId();
        startTripUseCase.execute(tripId, busId);
        // return ResponseEntity.ok().build();
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{tripId}/stops/{routeStopId}/confirm")
    public ResponseEntity<TripDetailResponse> confirmStop(@PathVariable UUID tripId, @PathVariable UUID routeStopId) {

        UUID busId = getCurrentBusId();
        TripDetailView response = confirmStopUseCase.execute(tripId, routeStopId, busId);
        TripDetailResponse tripDetailResponse = tripDetailMapper.toResponse(response);
        return ResponseEntity.ok(tripDetailResponse);
    }

    @PostMapping("/{tripId}/finish")
    public ResponseEntity<TripDetailResponse> finishTrip(@PathVariable UUID tripId){
        TripDetailView response = finishTripUseCase.execute(tripId);
        TripDetailResponse tripDetailResponse = tripDetailMapper.toResponse(response);
        return ResponseEntity.ok(tripDetailResponse);
    }

    private UUID getCurrentBusId() {
        return (UUID) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}

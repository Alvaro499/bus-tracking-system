package com.bustracking.tracking.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bustracking.tracking.application.usecase.ConfirmStopUseCase;
import com.bustracking.tracking.application.usecase.StartTripUseCase;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.infrastructure.mappers.TripDetailMapper;
import com.bustracking.tracking.infrastructure.web.dto.response.TripDetailResponse;

@RestController
@RequestMapping("/tracking/trips")
public class DriverTripCommandController {

    private final StartTripUseCase startTripUseCase;
    private final ConfirmStopUseCase confirmStopUseCase;

    //Mappers
    private final TripDetailMapper tripDetailMapper;

    public DriverTripCommandController(StartTripUseCase startTripUseCase, ConfirmStopUseCase confirmStopUseCase, TripDetailMapper tripDetailMapper) {
        this.startTripUseCase = startTripUseCase;
        this.confirmStopUseCase = confirmStopUseCase;
        this.tripDetailMapper = tripDetailMapper;
    }

    @PostMapping("/{tripId}/start")
    public  ResponseEntity<Void> startTrip(@PathVariable UUID tripId){

        // TODO: extraer busId del JWI a futuro
        UUID busId = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
        startTripUseCase.execute(tripId, busId);
        //return ResponseEntity.ok().build();
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{tripId}/stops/{routeStopId}/confirm")
    public ResponseEntity<TripDetailResponse> confirmStop(@PathVariable UUID tripId, @PathVariable UUID routeStopId){

        UUID busId = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
        // We need the busId so we can know which bus is confirming the stop
        TripDetailView response = confirmStopUseCase.execute(tripId, routeStopId, busId);
        TripDetailResponse tripDetailResponse = tripDetailMapper.toResponse(response);
        return ResponseEntity.ok(tripDetailResponse);
    }
}

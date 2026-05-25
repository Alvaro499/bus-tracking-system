package com.bustracking.tracking.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bustracking.tracking.application.usecase.StartTripUseCase;

@RestController
@RequestMapping("/tracking/trips")
public class DriverTripCommandController {
    private final StartTripUseCase startTripUseCase;

    public DriverTripCommandController(StartTripUseCase startTripUseCase) {
        this.startTripUseCase = startTripUseCase;
    }

    @PostMapping("/{tripId}/start")
    public  ResponseEntity<Void> startTrip(@PathVariable UUID tripId){

        // TODO: extraer busId del JWI a futuro
        UUID busId = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
        startTripUseCase.execute(tripId, busId);
        return ResponseEntity.ok().build();
    }
}

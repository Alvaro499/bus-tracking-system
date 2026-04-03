package com.bustracking.tracking.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bustracking.tracking.application.usecase.GetBusLocationUseCase;
import com.bustracking.tracking.application.usecase.UpdateBusLocationUseCase;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.infrastructure.web.dto.request.UpdateBusLocationRequest;
import com.bustracking.tracking.infrastructure.web.dto.response.BusLocationResponse;

@RestController
@RequestMapping("/tracking/buses")
class BusLocationController {
 
    private final UpdateBusLocationUseCase updateBusLocationUseCase;

    private final GetBusLocationUseCase getBusLocationUseCase;


    public BusLocationController(UpdateBusLocationUseCase updateBusLocationUseCase, GetBusLocationUseCase getBusLocationUseCase) {
        this.updateBusLocationUseCase = updateBusLocationUseCase;
        this.getBusLocationUseCase = getBusLocationUseCase;
    }

    @PostMapping("/{busId}/location")
    public ResponseEntity<Void> updateBusLocation(
            @PathVariable UUID busId,
            @RequestBody UpdateBusLocationRequest request) {

        updateBusLocationUseCase.execute(busId, request.lat(), request.lng());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{busId}/location")
    public ResponseEntity<BusLocationResponse> getBusLocation(
            @PathVariable UUID busId) {

        BusLocation location = getBusLocationUseCase.execute(busId);

        BusLocationResponse response = new BusLocationResponse(
            location.getBusId(),
            location.getGpsCoordinate().getLat(),
            location.getGpsCoordinate().getLng(),
            location.getUpdatedAt()
        );
        return ResponseEntity.ok(response);
    }
}
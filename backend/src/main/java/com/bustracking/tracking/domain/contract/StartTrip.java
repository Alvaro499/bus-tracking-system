package com.bustracking.tracking.domain.contract;

import java.util.UUID;


/**
 * Contract for starting a trip.
 * Implemented by the companies module, which owns the Trip aggregate
 * and its business rules.
 * 
 * Any possible errors or exceptions are going to be handled by GlobalExceptionHandler
 */

public interface StartTrip {
    
    void execute(UUID tripId, UUID busId);
}

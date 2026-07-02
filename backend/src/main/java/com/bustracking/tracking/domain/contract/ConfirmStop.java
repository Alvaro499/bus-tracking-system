package com.bustracking.tracking.domain.contract;

import java.util.UUID;

@FunctionalInterface
public interface ConfirmStop {
    void execute(UUID tripId, UUID stopId);
}
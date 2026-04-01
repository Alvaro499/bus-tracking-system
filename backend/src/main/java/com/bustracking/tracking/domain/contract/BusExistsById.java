package com.bustracking.tracking.domain.contract;

import java.util.UUID;

/**
 * Delegate (Functional Interface) - Represents a bus existence check
 * 
 * This contract defines the minimal requirement: does a bus with a given ID exist?
 * By using a delegate instead of a full interface, we prevent this contract from
 * growing with unrelated methods. It cannot accumulate "garbage" operations for testing.
 * 
 * The implementation is provided by the companies module through dependency injection.
 * 
 * @see com.bustracking.companies.infrastructure.config.TrackingDelegatesConfig
 */
@FunctionalInterface
public interface BusExistsById {
    
    boolean check(UUID busId);
}

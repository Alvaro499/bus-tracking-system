package com.bustracking.companies.domain.enums;

/**
 * Possible states of a trip in the system.
 * 
 * PLANNED: Trip scheduled but not yet started
 * IN_PROGRESS: Trip currently in progress
 * COMPLETED: Trip successfully completed
 * CANCELLED: Trip cancelled
 * REASSIGNED: Viaje reasignado a otro bus
 */
public enum TripStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    REASSIGNED
}

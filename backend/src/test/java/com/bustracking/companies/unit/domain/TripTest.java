package com.bustracking.companies.unit.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bustracking.companies.domain.enums.TripStatus;
import com.bustracking.companies.domain.model.Schedule;
import com.bustracking.companies.domain.model.Trip;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class TripTest {

    // =========================================================
    // Helpers
    // =========================================================

    private Trip aPlannedTrip() {
        return new Trip(UUID.randomUUID());
    }

    private Trip anInProgressTrip() {
        Trip trip = new Trip(UUID.randomUUID());
        trip.start(UUID.randomUUID());
        return trip;
    }

    private Trip aCancelledTrip() {
        Trip trip = new Trip(UUID.randomUUID());
        trip.cancel("Driver unavailable");
        return trip;
    }

    private Trip aCompletedTrip() {
        Trip trip = new Trip(UUID.randomUUID());
        trip.start(UUID.randomUUID());
        trip.complete();
        return trip;
    }

    // =========================================================
    // Happy Path & Invalid — Constructor
    // =========================================================

    @Test
    void shouldCreateTripWithCorrectInitialValues() {
        // Arrange
        UUID scheduleId = UUID.randomUUID();

        // Act
        Trip trip = new Trip(scheduleId);

        // Assert
        assertNotNull(trip.getId());
        assertEquals(scheduleId, trip.getScheduleId());
        assertEquals(TripStatus.PLANNED, trip.getStatus());
        assertEquals(LocalDate.now(), trip.getTripDate());
        assertNull(trip.getBusId());
        assertNull(trip.getActualStartTime());
        assertNull(trip.getActualEndTime());
        assertNull(trip.getCancellationReason());
    }

    @Test
    void shouldGenerateUniqueIdOnEachCreation() {
        // Arrange & Act
        Trip trip1 = aPlannedTrip();
        Trip trip2 = aPlannedTrip();

        // Assert
        assertNotEquals(trip1.getId(), trip2.getId());
    }

    @Test
    void shouldThrowValidationExceptionWhenScheduleIdIsNull() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> new Trip(null));
    }

    // =========================================================
    // Happy Path & Invalid — start()
    // =========================================================

    @Test
    void shouldStartTripWithCorrectFields() {
        // Arrange
        UUID busId = UUID.randomUUID();
        Trip trip = aPlannedTrip();

        // Act
        trip.start(busId);

        // Assert
        assertEquals(TripStatus.IN_PROGRESS, trip.getStatus());
        assertEquals(busId, trip.getBusId());
        assertNotNull(trip.getActualStartTime());
        assertNotNull(trip.getAssignedAt());
        assertNotNull(trip.getUpdatedAt());
    }

    @Test
    void shouldThrowWhenStartCalledOnInProgressTrip() {
        // Arrange
        Trip trip = anInProgressTrip();

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> trip.start(UUID.randomUUID()));
    }

    @Test
    void shouldThrowWhenStartCalledOnCancelledTrip() {
        // Arrange
        Trip trip = aCancelledTrip();

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> trip.start(UUID.randomUUID()));
    }

    @Test
    void shouldThrowWhenStartCalledOnCompletedTrip() {
        // Arrange
        Trip trip = aCompletedTrip();

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> trip.start(UUID.randomUUID()));
    }

    @Test
    void shouldThrowWhenBusIdIsNullOnStart() {
        // Arrange
        Trip trip = aPlannedTrip();

        // Act & Assert
        assertThrows(ValidationException.class, () -> trip.start(null));
    }

    // =========================================================
    // Happy Path & Invalid — cancel()
    // =========================================================

    @Test
    void shouldCancelPlannedTripWithCorrectFields() {
        // Arrange
        Trip trip = aPlannedTrip();
        String reason = "Driver unavailable";

        // Act
        trip.cancel(reason);

        // Assert
        assertEquals(TripStatus.CANCELLED, trip.getStatus());
        assertEquals(reason, trip.getCancellationReason());
        assertNotNull(trip.getUpdatedAt());
    }

    @Test
    void shouldCancelInProgressTripWithCorrectFields() {
        // Arrange
        Trip trip = anInProgressTrip();
        String reason = "Mechanical failure";

        // Act
        trip.cancel(reason);

        // Assert
        assertEquals(TripStatus.CANCELLED, trip.getStatus());
        assertEquals(reason, trip.getCancellationReason());
        assertNotNull(trip.getUpdatedAt());
    }

    @Test
    void shouldThrowWhenCancelCalledOnCancelledTrip() {
        // Arrange
        Trip trip = aCancelledTrip();

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> trip.cancel("Another reason"));
    }

    @Test
    void shouldThrowWhenCancelCalledOnCompletedTrip() {
        // Arrange
        Trip trip = aCompletedTrip();

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> trip.cancel("Too late"));
    }

    @Test
    void shouldThrowWhenCancellationReasonIsNull() {
        // Arrange
        Trip trip = aPlannedTrip();

        // Act & Assert
        assertThrows(ValidationException.class, () -> trip.cancel(null));
    }

    @Test
    void shouldThrowWhenCancellationReasonIsBlank() {
        // Arrange
        Trip trip = aPlannedTrip();

        // Act & Assert
        assertThrows(ValidationException.class, () -> trip.cancel("   "));
    }

    // =========================================================
    // Happy Path & Invalid — complete()
    // =========================================================

    @Test
    void shouldCompleteTripWithCorrectFields() {
        // Arrange
        Trip trip = anInProgressTrip();

        // Act
        trip.complete();

        // Assert
        assertEquals(TripStatus.COMPLETED, trip.getStatus());
        assertNotNull(trip.getActualEndTime());
        assertNotNull(trip.getUpdatedAt());
    }

    @Test
    void shouldThrowWhenCompleteCalledOnPlannedTrip() {
        // Arrange
        Trip trip = aPlannedTrip();

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> trip.complete());
    }

    @Test
    void shouldThrowWhenCompleteCalledOnCancelledTrip() {
        // Arrange
        Trip trip = aCancelledTrip();

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> trip.complete());
    }

    // =========================================================
    // Happy Path & Edge Cases — complete(Schedule) & calculateDelayAgainst
    // =========================================================

    @Test
    void shouldCompleteTripWithScheduleAndCalculateDelay() {
        // Arrange
        Trip trip = anInProgressTrip();
        LocalTime pastDeparture = LocalTime.now().minusHours(2);
        Schedule schedule = new Schedule(
                trip.getScheduleId(),    // id
                UUID.randomUUID(),      // routeId
                pastDeparture,          // departureTime
                30,  // estimatedDurationMin
                1,              //dayOfWeek
                LocalDate.now(),           // startDate
                null,              //endDate
                true,            //isActive
                LocalDateTime.now(),       // 9. createdAt
                LocalDateTime.now()        // 10. updatedAt
        );
        // Act
        trip.complete(schedule);

        // Assert
        assertEquals(TripStatus.COMPLETED, trip.getStatus());
        assertNotNull(trip.getActualEndTime());
        assertNotNull(trip.getDelayMinutes());
        assertTrue(trip.getDelayMinutes() > 0, "Delay should be positive when arrival is late");
    }

    @Test
    void shouldCalculateZeroDelay_WhenArrivingOnTimeOrEarly() {
        // Arrange
        Trip trip = new Trip(UUID.randomUUID());
        trip.start(UUID.randomUUID());

        // Salida esperada en el futuro / estimada suficientemente holgada
        LocalTime futureDeparture = LocalTime.now().plusHours(1);
        Schedule schedule = new Schedule(
                trip.getScheduleId(), // 1. id
                UUID.randomUUID(), // 2. routeId
                futureDeparture, // 3. departureTime
                60, // 4. estimatedDurationMin
                1, // 5. dayOfWeek
                LocalDate.now(), // 6. startDate
                null, // 7. endDate
                true, // 8. isActive
                LocalDateTime.now(), // 9. createdAt
                LocalDateTime.now() // 10. updatedAt
        );

        // Act
        trip.complete(schedule);

        // Assert
        assertEquals(0, trip.getDelayMinutes(), "Delay should be 0 if the trip arrived early/on time");
    }

    @Test
    void shouldReturnZeroDelay_WhenScheduleIsNull() {
        // Arrange
        Trip trip = anInProgressTrip();
        trip.complete(); // completed without schedule

        // Act
        int delay = trip.calculateDelayAgainst(null);

        // Assert
        assertEquals(0, delay, "Delay calculation should be resilient and return 0 if Schedule is null");
    }
}
package com.bustracking.companies.domain.model;

import com.bustracking.companies.domain.enums.TripStatus;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    private UUID id;

    private UUID scheduleId;

    private UUID busId;

    private LocalDate tripDate;

    private String cancellationReason;

    private TripStatus status;

    private LocalTime actualStartTime;

    private LocalTime actualEndTime;

    private Integer delayMinutes;

    private LocalDateTime assignedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Trip(UUID scheduleId) {

        validateScheduleId(scheduleId);
        this.id = UUID.randomUUID();
        this.scheduleId = scheduleId;
        this.busId = null;
        this.tripDate = LocalDate.now();
        this.status = TripStatus.PLANNED;
        this.actualStartTime = null;
        this.actualEndTime = null;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        cancellationReason = null;
        delayMinutes = null;
    }

    // VALIDATION METHODS
    private void validateScheduleId(UUID scheduleId) {
        if (scheduleId == null) {
            throw new ValidationException(
                    ErrorCode.MISSING_REQUIRED_FIELD,
                    "Schedule ID is required",
                    "Schedule ID cannot be null");
        }
    }

    // BUSINESS METHODS

    public void start(UUID busId) {
        if (this.status != TripStatus.PLANNED) {
            throw new BusinessRuleException(
                    ErrorCode.INVALID_STATE,
                    "Trip can only be started from PLANNED status");
        }
        if (busId == null) {
            throw new ValidationException(
                    ErrorCode.MISSING_REQUIRED_FIELD,
                    "Bus ID is required to start a trip",
                    "Bus ID cannot be null");
        }
        this.busId = busId;
        this.status = TripStatus.IN_PROGRESS;
        this.actualStartTime = LocalTime.now();
        this.assignedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String cancellationReason) {
        if (this.status == TripStatus.CANCELLED || this.status == TripStatus.COMPLETED) {
            throw new BusinessRuleException(
                    ErrorCode.INVALID_STATE,
                    "Trip cannot be cancelled from its current status");
        }
        if (cancellationReason == null || cancellationReason.isBlank()) {
            throw new ValidationException(
                    ErrorCode.MISSING_REQUIRED_FIELD,
                    "Cancellation reason is required",
                    "Cancellation reason cannot be null or blank");
        }

        this.status = TripStatus.CANCELLED;
        this.cancellationReason = cancellationReason;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != TripStatus.IN_PROGRESS) {
            throw new BusinessRuleException(
                    ErrorCode.INVALID_STATE,
                    "Trip only can be completed from IN_PROGRESS status");
        }
        this.actualEndTime = LocalTime.now();
        this.status = TripStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public int calculateDelayAgainst(Schedule schedule) {
        if (this.actualEndTime == null || schedule == null || schedule.getDepartureTime() == null) {
            return 0;
        }

        int estimatedDuration = schedule.getEstimatedDurationMin() != null
                ? schedule.getEstimatedDurationMin()
                : 0;

        // Expected date and hourDate and ho y hora esperadas de salida y llegada
        LocalDateTime scheduledStart = LocalDateTime.of(this.tripDate, schedule.getDepartureTime());
        LocalDateTime scheduledArrival = scheduledStart.plusMinutes(estimatedDuration);

        // Fecha y hora real de llegada
        LocalDateTime actualArrival = LocalDateTime.of(this.tripDate, this.actualEndTime);

        // Si la hora de llegada real es menor que la hora de salida (ej: salió 23:30,
        // llegó 00:30), sumamos 1 día
        LocalTime referenceStart = (this.actualStartTime != null) ? this.actualStartTime : schedule.getDepartureTime();
        if (this.actualEndTime.isBefore(referenceStart)) {
            actualArrival = actualArrival.plusDays(1);
        }

        // Retorno en minutos (mínimo 0)
        long delay = Duration.between(scheduledArrival, actualArrival).toMinutes();
        return Math.max(0, (int) delay);
    }

    public void complete(Schedule schedule) {
        if (this.status != TripStatus.IN_PROGRESS) {
            throw new BusinessRuleException(
                    ErrorCode.INVALID_STATE,
                    "Trip only can be completed from IN_PROGRESS status");
        }

        this.actualEndTime = LocalTime.now();
        this.status = TripStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();

        // Guardamos el retraso directamente en la entidad
        this.delayMinutes = calculateDelayAgainst(schedule);
    }
}
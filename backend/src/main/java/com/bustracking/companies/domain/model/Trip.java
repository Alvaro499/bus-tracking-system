package com.bustracking.companies.domain.model;

import com.bustracking.companies.domain.enums.TripStatus;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
                "Schedule ID cannot be null"
            );
        }
    }

    // BUSINESS METHODS

    public void start(UUID busId) {
        if (this.status != TripStatus.PLANNED) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Trip can only be started from PLANNED status"
            );
        }
        if (busId == null) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "Bus ID is required to start a trip",
                "Bus ID cannot be null"
            );
        }
        this.busId = busId;
        this.status = TripStatus.IN_PROGRESS;
        this.actualStartTime = LocalTime.now();
        this.assignedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String cancellationReason){
        if(this.status == TripStatus.CANCELLED || this.status == TripStatus.COMPLETED) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Trip cannot be cancelled from its current status"
            );
        }
        if (cancellationReason == null || cancellationReason.isBlank()) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "Cancellation reason is required",
                "Cancellation reason cannot be null or blank"
            );
        }

        this.status = TripStatus.CANCELLED;
        this.cancellationReason = cancellationReason;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(){
        if(this.status != TripStatus.IN_PROGRESS){
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Trip only can be completed from IN_PROGRESS status"
            );
        }
        this.actualEndTime = LocalTime.now();
        this.status = TripStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void registerDelay(Integer minutes) {
        if (minutes == null || minutes < 0) {
            throw new ValidationException(
                ErrorCode.INVALID_INPUT,
                "Invalid delay minutes",
                "Delay minutes cannot be null or negative"
            );
        }
        this.delayMinutes = minutes;
        this.updatedAt = LocalDateTime.now();
    }
}
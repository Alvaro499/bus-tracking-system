package com.bustracking.companies.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.companies.domain.enums.BusStatus;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Bus {

    private UUID id;

    private UUID companyId;

    private String plate;

    private String internalNumber;

    private Boolean hasRamp;

    private BusStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Bus(UUID companyId, String plate, String internalNumber, Boolean hasRamp){
        validatePlate(plate);
        validateInternalNumber(internalNumber);
        this.id = UUID.randomUUID();
        this.companyId = companyId;
        this.plate = plate;
        this.internalNumber = internalNumber;
         // every new bus is created as inactive
        this.status = BusStatus.INACTIVE;
        this.hasRamp = hasRamp;
    }

    private void validatePlate(String plate){
        if (plate == null){
            throw new ValidationException(ErrorCode.MISSING_REQUIRED_FIELD, "Plate has no valid value", "Plate number cannot be null");
        }

        if (plate.isBlank()){
            throw new ValidationException(ErrorCode.INVALID_INPUT, "Plate can not be empty", "Plate number cannot be blank");
        }

        if(plate.length() < 5 || plate.length() > 10){
            throw new ValidationException(ErrorCode.INVALID_INPUT, "Plate has no valid values", "Plate number must be between 5 and 10 characters");
        }
    }

    private void validateInternalNumber(String internalNumber) {
        // it could be null, as it is an optional field
        if (internalNumber == null) {return;}

        if (internalNumber.isBlank()){
            throw new ValidationException(ErrorCode.INVALID_INPUT, "Internal number has no valid values", "Internal number cannot be empty if provided");
        }
            
        if (internalNumber.length() > 20){
            throw new ValidationException(ErrorCode.INVALID_INPUT, "Internal number has no valid values", "Internal number cannot exceed 20 characters");
        }

        if (internalNumber.length() < 3) {
            throw new ValidationException(ErrorCode.INVALID_INPUT, "Internal number has no valid values", "Internal number must be at least 3 characters long");
            
        }
    }

    public void activate() {
        if (this.status == BusStatus.MAINTENANCE) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Bus cannot be activated while in maintenance"
            );
        }
        if (this.status == BusStatus.ACTIVE) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Bus is already active"
            );
        }
        this.status = BusStatus.ACTIVE;
    }

    public void deactivate() {
        if (this.status == BusStatus.INACTIVE) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Bus is already inactive"
            );
        }
        this.status = BusStatus.INACTIVE;
    }

    public void sendToMaintenance() {
        if (this.status == BusStatus.MAINTENANCE) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Bus is already in maintenance"
            );
        }
        this.status = BusStatus.MAINTENANCE;
    }
}

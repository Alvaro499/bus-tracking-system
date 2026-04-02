package com.bustracking.companies.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.companies.domain.enums.BusStatus;
import com.bustracking.companies.domain.valueobjects.InternalNumber;
import com.bustracking.companies.domain.valueobjects.Plate;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Bus {

    private UUID id;

    private UUID companyId;

    private Plate plate;

    private InternalNumber internalNumber;

    private Boolean hasRamp;

    private BusStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Bus(UUID companyId, String plate, String internalNumber, Boolean hasRamp){
        this.id = UUID.randomUUID();
        this.companyId = companyId;
        this.plate = new Plate(plate);
        this.internalNumber = new InternalNumber(internalNumber);
         // every new bus is created as inactive
        this.status = BusStatus.INACTIVE;
        this.hasRamp = hasRamp;
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

    public void finishMaintenance() {
        if (this.status != BusStatus.MAINTENANCE) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Bus is not in maintenance"
            );
        }
        this.status = BusStatus.INACTIVE;
    }
}

package com.bustracking.companies.unit.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.bustracking.companies.domain.enums.BusStatus;
import com.bustracking.companies.domain.model.Bus;
import com.bustracking.companies.domain.valueobjects.InternalNumber;
import com.bustracking.companies.domain.valueobjects.Plate;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ValidationException;

public class BusTest {
    
    // Common data test
    private final UUID validCompanyId = UUID.randomUUID();
    private final String validPlate = "CRC123";
    private final String validInternalNumber = "BUS-01";

    //==========================================================
    // Happy Path Tests
    //==========================================================

    @Test
    void shouldCreateBusWithValidValues() {
        Bus bus = new Bus(validCompanyId, validPlate, validInternalNumber, true);

        assertEquals(validCompanyId, bus.getCompanyId());
        assertEquals(new Plate(validPlate), bus.getPlate());
        assertEquals(new InternalNumber(validInternalNumber), bus.getInternalNumber());
        assertTrue(bus.getHasRamp());
        assertEquals(BusStatus.INACTIVE, bus.getStatus());
        assertNotNull(bus.getId());
    }

    @Test
    void shouldCreateBusWithoutInternalNumber() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);

        assertNull(bus.getInternalNumber().getValue());
        assertEquals(BusStatus.INACTIVE, bus.getStatus());
    }

    // =========================================================
    // Bus State Transitions - activate
    // =========================================================

    @Test
    void shouldActivateBusWhenInactive() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        assertEquals(BusStatus.ACTIVE, bus.getStatus());
    }

    @Test
    void shouldThrowWhenActivatingAlreadyActiveBus() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();

        // equivalent -> assertThrows(BusinessRuleException.class, () -> bus.activate());
        assertThrows(BusinessRuleException.class, bus::activate);
    }

    @Test
    void shouldThrowWhenActivatingBusInMaintenance() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        bus.sendToMaintenance();

        assertThrows(BusinessRuleException.class, bus::activate);
    }

    // =========================================================
    // State Transitions - deactivate
    // =========================================================

    @Test
    void shouldDeactivateBusWhenActive() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        bus.deactivate();

        assertEquals(BusStatus.INACTIVE, bus.getStatus());
    }

    @Test
    void shouldThrowWhenDeactivatingAlreadyInactiveBus() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);

        assertThrows(BusinessRuleException.class, bus::deactivate);
    }

    @Test
    void shouldDeactivateBusWhenInMaintenance() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        bus.sendToMaintenance();
        bus.deactivate();

        assertEquals(BusStatus.INACTIVE, bus.getStatus());
    }

    // =========================================================
    // Transiciones de estado - sendToMaintenance
    // =========================================================

    @Test
    void shouldSendBusToMaintenanceWhenActive() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        bus.sendToMaintenance();

        assertEquals(BusStatus.MAINTENANCE, bus.getStatus());
    }

    @Test
    void shouldThrowWhenSendingToMaintenanceAlreadyInMaintenance() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        bus.sendToMaintenance();

        assertThrows(BusinessRuleException.class, bus::sendToMaintenance);
    }

    @Test
    void shouldSendBusToMaintenanceWhenInactive() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.sendToMaintenance();

        assertEquals(BusStatus.MAINTENANCE, bus.getStatus());
    }

    // =========================================================
    // Invalid CompanyId and HasRamp
    // =========================================================

    @Test
    void shouldThrowWhenCompanyIdIsNull() {
        assertThrows(ValidationException.class, () ->
            new Bus(null, validPlate, validInternalNumber, true)
        );
    }

    @Test
    void shouldThrowWhenHasRampIsNull() {
        assertThrows(ValidationException.class, () ->
            new Bus(validCompanyId, validPlate, validInternalNumber, null)
        );
    }

    // =========================================================
    // Timestamps
    // =========================================================

    @Test
    void shouldInitializeCreatedAtAndUpdatedAtOnConstruction() {
        Bus bus = new Bus(validCompanyId, validPlate, validInternalNumber, true);

        assertNotNull(bus.getCreatedAt());
        assertNotNull(bus.getUpdatedAt());
        assertEquals(bus.getCreatedAt(), bus.getUpdatedAt());
    }
}
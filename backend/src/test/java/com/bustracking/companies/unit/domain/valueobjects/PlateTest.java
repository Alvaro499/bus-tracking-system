package com.bustracking.companies.unit.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bustracking.companies.domain.valueobjects.Plate;
import com.bustracking.shared.exception.ValidationException;

class PlateTest {

    // =========================================================
    // Happy Path Tests
    // =========================================================

    @Test
    void shouldCreatePlateWithValidValue() {
        String validPlate = "CRC123";
        Plate plate = new Plate(validPlate);

        assertEquals(validPlate, plate.getValue());
    }

    @Test
    void shouldCreatePlateAtMinimumLength() {
        String minPlate = "AB123";
        Plate plate = new Plate(minPlate);
        assertEquals(minPlate, plate.getValue());
    }

    @Test
    void shouldCreatePlateAtMaximumLength() {
        String maxPlate = "ABC1234567";
        Plate plate = new Plate(maxPlate);
        assertEquals(maxPlate, plate.getValue());
    }

    @Test
    void shouldConsiderTwoPlatesWithSameValueAsEqual() {
        Plate plate1 = new Plate("CRC123");
        Plate plate2 = new Plate("CRC123");

        assertEquals(plate1, plate2);
        assertEquals(plate1.hashCode(), plate2.hashCode());
    }

    @Test
    void shouldHaveDifferentHashCodeForDifferentValues() {
        Plate plate1 = new Plate("CRC123");
        Plate plate2 = new Plate("ABC456");

        assertNotEquals(plate1, plate2);
    }

    // =========================================================
    // Invalid Plate
    // =========================================================

    @Test
    void shouldThrowWhenPlateIsNull() {
        assertThrows(ValidationException.class, () ->
            new Plate(null)
        );
    }

    @Test
    void shouldThrowWhenPlateIsBlank() {
        assertThrows(ValidationException.class, () ->
            new Plate("   ")
        );
    }

    @Test
    void shouldThrowWhenPlateTooShort() {
        assertThrows(ValidationException.class, () ->
            new Plate("AB1")
        );
    }

    @Test
    void shouldThrowWhenPlateTooLong() {
        assertThrows(ValidationException.class, () ->
            new Plate("ABC12345678")
        );
    }

    @Test
    void shouldThrowWhenPlateIsEmpty() {
        assertThrows(ValidationException.class, () ->
            new Plate("")
        );
    }
}

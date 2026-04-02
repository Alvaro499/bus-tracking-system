package com.bustracking.companies.unit.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bustracking.companies.domain.valueobjects.InternalNumber;
import com.bustracking.shared.exception.ValidationException;

class InternalNumberTest {

    // =========================================================
    // Happy Path Tests
    // =========================================================

    @Test
    void shouldCreateInternalNumberWithValidValue() {
        String validNumber = "BUS-01";
        InternalNumber internalNumber = new InternalNumber(validNumber);

        assertEquals(validNumber, internalNumber.getValue());
    }

    @Test
    void shouldCreateInternalNumberAsNull() {
        InternalNumber internalNumber = new InternalNumber(null);

        assertNull(internalNumber.getValue());
        assertTrue(internalNumber.isEmpty());
    }

    @Test
    void shouldCreateInternalNumberAtMinimumLength() {
        String minNumber = "ABC";
        InternalNumber internalNumber = new InternalNumber(minNumber);
        assertEquals(minNumber, internalNumber.getValue());
    }

    @Test
    void shouldCreateInternalNumberAtMaximumLength() {
        String maxNumber = "A".repeat(20);
        InternalNumber internalNumber = new InternalNumber(maxNumber);
        assertEquals(maxNumber, internalNumber.getValue());
    }

    @Test
    void shouldConsiderTwoInternalNumbersWithSameValueAsEqual() {
        InternalNumber number1 = new InternalNumber("BUS-01");
        InternalNumber number2 = new InternalNumber("BUS-01");

        assertEquals(number1, number2);
        assertEquals(number1.hashCode(), number2.hashCode());
    }

    @Test
    void shouldConsiderTwoNullInternalNumbersAsEqual() {
        InternalNumber number1 = new InternalNumber(null);
        InternalNumber number2 = new InternalNumber(null);

        assertEquals(number1, number2);
    }

    @Test
    void shouldHaveDifferentHashCodeForDifferentValues() {
        InternalNumber number1 = new InternalNumber("BUS-01");
        InternalNumber number2 = new InternalNumber("BUS-02");

        assertNotEquals(number1, number2);
    }

    // =========================================================
    // Invalid InternalNumber
    // =========================================================

    @Test
    void shouldThrowWhenInternalNumberIsBlank() {
        assertThrows(ValidationException.class, () ->
            new InternalNumber("   ")
        );
    }

    @Test
    void shouldThrowWhenInternalNumberTooShort() {
        assertThrows(ValidationException.class, () ->
            new InternalNumber("AB")
        );
    }

    @Test
    void shouldThrowWhenInternalNumberTooLong() {
        assertThrows(ValidationException.class, () ->
            new InternalNumber("A".repeat(21))
        );
    }

    @Test
    void shouldThrowWhenInternalNumberIsEmpty() {
        assertThrows(ValidationException.class, () ->
            new InternalNumber("")
        );
    }

    @Test
    void shouldNotThrowWhenInternalNumberIsNull() {
        // Optional field, null is allowed
        assertDoesNotThrow(() -> new InternalNumber(null));
    }
}

package com.bustracking.companies.domain.valueobjects;

import java.util.Objects;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;

/**
 * Value Object representing a bus plate/registration number.
 * Ensures the plate follows the business rules:
 * - Not null
 * - Not blank
 * - Between 5 and 10 characters
 */
public final class Plate {

    private final String value;

    public Plate(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "Plate has no valid value",
                "Plate number cannot be null"
            );
        }

        if (value.isBlank()) {
            throw new ValidationException(
                ErrorCode.INVALID_INPUT,
                "Plate can not be empty",
                "Plate number cannot be blank"
            );
        }

        if (value.length() < 5 || value.length() > 10) {
            throw new ValidationException(
                ErrorCode.INVALID_INPUT,
                "Plate has no valid values",
                "Plate number must be between 5 and 10 characters"
            );
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plate)) return false;
        Plate plate = (Plate) o;
        return Objects.equals(value, plate.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

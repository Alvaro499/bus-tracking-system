package com.bustracking.companies.domain.valueobjects;

import java.util.Objects;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;

/**
 * Value Object representing a bus internal number.
 * Ensures the internal number follows the business rules:
 * - Can be null (optional field)
 * - If provided, cannot be blank
 * - Between 3 and 20 characters
 */
public final class InternalNumber {

    private final String value;

    public InternalNumber(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        // Optional field - null is allowed
        if (value == null) {
            return;
        }

        if (value.isBlank()) {
            throw new ValidationException(
                ErrorCode.INVALID_INPUT,
                "Internal number has no valid values",
                "Internal number cannot be empty if provided"
            );
        }

        if (value.length() < 3) {
            throw new ValidationException(
                ErrorCode.INVALID_INPUT,
                "Internal number has no valid values",
                "Internal number must be at least 3 characters long"
            );
        }

        if (value.length() > 20) {
            throw new ValidationException(
                ErrorCode.INVALID_INPUT,
                "Internal number has no valid values",
                "Internal number cannot exceed 20 characters"
            );
        }
    }

    public String getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InternalNumber)) return false;
        InternalNumber that = (InternalNumber) o;
        return Objects.equals(value, that.value);
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

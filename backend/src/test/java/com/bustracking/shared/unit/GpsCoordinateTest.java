package com.bustracking.shared.unit;



import com.bustracking.shared.exception.ValidationException;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GpsCoordinateTest {

    // =========================================================
    // Happy Path Tests
    // =========================================================

    @Test
    void shouldCreateCoordinateWithValidValues() {
        BigDecimal lat = new BigDecimal("9.934739");
        BigDecimal lng = new BigDecimal("-84.087502");

        GpsCoordinate coordinate = new GpsCoordinate(lat, lng);

        assertEquals(lat, coordinate.getLat());
        assertEquals(lng, coordinate.getLng());
    }

    @Test
    void shouldCreateCoordinateAtExactBoundaries() {
        GpsCoordinate topLeft     = new GpsCoordinate(new BigDecimal("90"),  new BigDecimal("-180"));
        GpsCoordinate bottomRight = new GpsCoordinate(new BigDecimal("-90"), new BigDecimal("180"));

        assertEquals(new BigDecimal("90"),   topLeft.getLat());
        assertEquals(new BigDecimal("-180"), topLeft.getLng());
        assertEquals(new BigDecimal("-90"),  bottomRight.getLat());
        assertEquals(new BigDecimal("180"),  bottomRight.getLng());
    }

    @Test
    void shouldCreateCoordinateAtOrigin() {
        GpsCoordinate origin = new GpsCoordinate(BigDecimal.ZERO, BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, origin.getLat());
        assertEquals(BigDecimal.ZERO, origin.getLng());
    }

    // =========================================================
    // INVALID LATITUDE
    // =========================================================

    @Test
    void shouldThrowWhenLatitudeIsNull() {
        assertThrows(ValidationException.class, () ->
            new GpsCoordinate(null, new BigDecimal("10.0"))
        );
    }

    @Test
    void shouldThrowWhenLatitudeExceedsMaximum() {
        assertThrows(ValidationException.class, () ->
            new GpsCoordinate(new BigDecimal("90.000001"), new BigDecimal("0"))
        );
    }

    @Test
    void shouldThrowWhenLatitudeBelowMinimum() {
        assertThrows(ValidationException.class, () ->
            new GpsCoordinate(new BigDecimal("-90.000001"), new BigDecimal("0"))
        );
    }

    // =========================================================
    // Invalid Longitude
    // =========================================================

    /*
        Other more strict examples:

            assertNotNull(exception);
            assertEquals(ErrorCode.MISSING_REQUIRED_FIELD, exception.getErrorCode()); --> recommended because is what the exception handlers use to:
                - Register error type of logs
                - Return HTTP code to client (e.g. 400 Bad Request)

            assertEquals("Latitude and Longitud are missing", exception.getMessage());
            assertNotNull(exception.getDetails());
    */

    @Test
    void shouldThrowWhenLongitudeIsNull() {
        assertThrows(ValidationException.class, () ->
            new GpsCoordinate(new BigDecimal("10.0"), null)
        );
    }

    @Test
    void shouldThrowWhenLongitudeExceedsMaximum() {
        assertThrows(ValidationException.class, () ->
            new GpsCoordinate(new BigDecimal("0"), new BigDecimal("180.000001"))
        );
    }

    @Test
    void shouldThrowWhenLongitudeBelowMinimum() {
        assertThrows(ValidationException.class, () ->
            new GpsCoordinate(new BigDecimal("0"), new BigDecimal("-180.000001"))
        );
    }

    // =========================================================
    // Both Null
    // =========================================================

    @Test
    void shouldThrowWhenBothAreNull() {
        assertThrows(ValidationException.class, () ->
            new GpsCoordinate(null, null)
        );
    }

    // =========================================================
    // Equals and HashCode
    // =========================================================

    @Test
    void shouldBeEqualWhenSameValues() {
        GpsCoordinate a = new GpsCoordinate(new BigDecimal("9.9"), new BigDecimal("-84.0"));
        GpsCoordinate b = new GpsCoordinate(new BigDecimal("9.9"), new BigDecimal("-84.0"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValues() {
        GpsCoordinate a = new GpsCoordinate(new BigDecimal("9.9"), new BigDecimal("-84.0"));
        GpsCoordinate b = new GpsCoordinate(new BigDecimal("1.0"), new BigDecimal("-84.0"));

        assertNotEquals(a, b);
    }
}
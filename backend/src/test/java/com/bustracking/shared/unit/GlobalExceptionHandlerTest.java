package com.bustracking.shared.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ExternalServiceException;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.exception.ValidationException;
import com.bustracking.shared.infrastructure.error.ErrorResponse;
import com.bustracking.shared.infrastructure.error.GlobalExceptionHandler;


class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn404WhenNotFoundExceptionIsThrown() {
        NotFoundException ex = new NotFoundException(
            ErrorCode.RESOURCE_NOT_FOUND,  // genérico, no BUS_NOT_FOUND
            "Resource not found",
            "dev details"
        );
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);
        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getCode());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldReturn400WhenValidationExceptionIsThrown() {
        ValidationException ex = new ValidationException(
            ErrorCode.MISSING_REQUIRED_FIELD,
            "Field required",
            "dev details"
        );
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturn422WhenBusinessRuleExceptionIsThrown() {
        BusinessRuleException ex = new BusinessRuleException(
            ErrorCode.INVALID_STATE,
            "Invalid state"
        );
        ResponseEntity<ErrorResponse> response = handler.handleBusinessRule(ex);
        assertEquals(422, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturn502WhenExternalServiceExceptionIsThrown() {
        ExternalServiceException ex = new ExternalServiceException(
            ErrorCode.EXTERNAL_SERVICE_ERROR,
            "Service unavailable",
            "dev details"
        );
        ResponseEntity<ErrorResponse> response = handler.handleExternalService(ex);
        assertEquals(502, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }


    /**
     * // Un solo test que verifica la integración Spring + handler
// Podría vivir en GlobalExceptionHandlerTest o en un test propio
@Test
void shouldReturn404WhenUseCaseThrowsNotFoundException() {
    when(getBusLocationUseCase.execute(any()))
        .thenThrow(new NotFoundException(...));

    mockMvc.perform(get("/tracking/buses/{id}/location", validBusId))
        .andExpect(status().isNotFound());
}
     */
}
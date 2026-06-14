package com.bustracking.tracking.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.testinfrastructure.ControllerIntegrationTest;
import com.bustracking.tracking.application.usecase.StartTripUseCase;
import com.bustracking.tracking.infrastructure.web.controller.DriverTripCommandController;

@WebMvcTest(DriverTripCommandController.class)
class DriverTripCommandControllerTest extends ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartTripUseCase startTripUseCase;

    // Start Data
    UUID validTripId = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");
    // =========================================================
    // POST /tracking/trips/{tripId}/start - Happy Path
    // =========================================================

    @Test
    void shouldReturn204WhenTripIsStartedSuccessfully() throws Exception {
        // Arrange
        doNothing().when(startTripUseCase).execute(eq(validTripId), any());

        // Act & Assert
        mockMvc.perform(post("/tracking/trips/{tripId}/start", validTripId))
            .andExpect(status().isNoContent());

        // Verifica que el caso de uso se llamó con el tripId y cualquier busId
        verify(startTripUseCase).execute(eq(validTripId), any());
    }

    @Test
    void shouldReturn400WhenTripIdIsNotValidUUID() throws Exception {
        mockMvc.perform(post("/tracking/trips/{tripId}/start", "not-a-valid-uuid"))
            .andExpect(status().isBadRequest());

        // El caso de uso nunca debe ejecutarse con un UUID inválido
        verifyNoInteractions(startTripUseCase);
    }
}

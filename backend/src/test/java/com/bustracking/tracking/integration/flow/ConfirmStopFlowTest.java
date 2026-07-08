package com.bustracking.tracking.integration.flow;

import static com.bustracking.shared.testinfrastructure.TestSqlScripts.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.bustracking.shared.testinfrastructure.FlowIntegrationTest;
import com.bustracking.tracking.infrastructure.web.dto.response.TripDetailResponse;
import com.bustracking.tracking.infrastructure.web.dto.response.TripStopDetailResponse;

@Sql(scripts = {
        CLEANUP,
        BASE,
        TRIP_COMMON,
        TRIP_DETAIL
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ConfirmStopFlowTest extends FlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // IDs de los scripts SQL
    private static final UUID TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655449999");
    private static final UUID ROUTE_STOP_ID = UUID.fromString("960e8400-e29b-41d4-a716-446655440002");
    private static final UUID BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final UUID NON_EXISTENT_ID = UUID.randomUUID();

    @Test
    void shouldConfirmStopAndReturnUpdatedDetail() throws Exception {

        // Arrange and Act
        MvcResult result = mockMvc.perform(post("/tracking/trips/{tripId}/stops/{routeStopId}/confirm",
                TRIP_ID, ROUTE_STOP_ID)
                .with(withDriverCookie(BUS_ID)))
                .andExpect(status().isOk())
                .andReturn();

        // We parse the response to TripDetailResponse
        String json = result.getResponse().getContentAsString();
        TripDetailResponse response = objectMapper.readValue(json, TripDetailResponse.class);

        // We find the target stop we want to assert in the response
        TripStopDetailResponse targetStop = response.stops().stream()
                .filter(s -> s.routeStop().id().equals(ROUTE_STOP_ID))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Parada no encontrada en la respuesta"));

        // Assert
        assertNotNull(targetStop.completedAt(), "La parada debería estar completada");
    }

}

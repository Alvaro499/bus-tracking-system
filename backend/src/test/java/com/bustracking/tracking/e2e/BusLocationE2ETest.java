package com.bustracking.tracking.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.testinfrastructure.E2EIntegrationTest;


/**
 * E2E Test: Bus Location Flow
 *
 * Business story:
 * "A bus sends its location and a user can query it"
 *
 * Flow:
 * POST /tracking/buses/{busId}/location  → device sends location
 * GET  /tracking/buses/{busId}/location  → user queries location
 *
 * Covers: HU-01 (ver buses en el mapa) + HU-16 (enviar ubicación desde el bus)
 */

@Sql({
    "/test-data/fixtures-shared.sql",
    "/test-data/tracking-fixtures.sql"
})
public class BusLocationE2ETest extends E2EIntegrationTest{
    
    @Autowired
    private MockMvc mockMvc;

    // Fixed UUID from tracking-fixtures.sql
    private static final UUID BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");

    private final ObjectMapper objectMapper = new ObjectMapper();

    // TextBlock instead of ObjectMapper for simplicity, since the request body is simple and we don't need to reuse it
    //private static final String VALID_LOCATION_BODY = """
    //    {"lat": 9.934739, "lng": -84.087502}
    //    """;

    @Test
    void shouldSendAndRetrieveBusLocation() throws Exception{

        String VALID_LOCATION_BODY = objectMapper.writeValueAsString(
            new Object() {
                public final BigDecimal lat = new BigDecimal("9.934739");
                public final BigDecimal lng = new BigDecimal("-84.087502");
            }
        );

        // Step 1: Device sends bus location (HU-16)
        mockMvc.perform(post("/tracking/buses/{busId}/location", BUS_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_LOCATION_BODY))
            .andExpect(status().isOk());

            // Step 2: User queries bus location (HU-01)
        mockMvc.perform(get("/tracking/buses/{busId}/location", BUS_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.busId").value(BUS_ID.toString()))
            .andExpect(jsonPath("$.lat").value(9.934739))
            .andExpect(jsonPath("$.lng").value(-84.087502))
            .andExpect(jsonPath("$.updatedAt").exists());
    }
}

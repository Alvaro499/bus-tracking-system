package com.bustracking.tracking.integration.flow;

import static com.bustracking.shared.testinfrastructure.TestSqlScripts.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.testinfrastructure.FlowIntegrationTest;

/**
 * Flow Test: Start Trip
 *
 * Business story:
 * "A driver selects a planned trip and starts it.
 * The trip disappears from today's planned list
 * and becomes the active trip of the bus."
 * 
 * Flow:
 * 1. GET /tracking/trips/today → trip appears as PLANNED
 * 2. POST /tracking/trips/{tripId}/start → trip changes to IN_PROGRESS
 * 3. GET /tracking/trips/today → started trip no longer listed
 * 4. GET /tracking/trips/active → (pending) returns active trip
 *
 * Covers: HU-17 (CA-02, CA-03)
 */
@Sql(scripts = {
        CLEANUP,
        BASE,
        TRIP_COMMON,
        PLANNED_TRIPS
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StartTripFlowTest extends FlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // UUIDs from tracking-base.sql and tracking-trips.sql
    private static final UUID BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final UUID PLANNED_TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");
    private static final UUID NON_EXISTENT_TRIP_ID = UUID.randomUUID();

    // =========================================================
    // POST /tracking/trips/{tripId}/start - Happy Path
    // =========================================================

    @Test
    void shouldStartTripAndRemoveFromTodayList() throws Exception {

        // Step 1: verify trip is in planned list before starting
        mockMvc.perform(get("/tracking/trips/today")
                .with(withDriverCookie(BUS_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id")
                        .value(hasItem(PLANNED_TRIP_ID.toString())));

        // Paso 1: Iniciar el viaje
        mockMvc.perform(post("/tracking/trips/{tripId}/start", PLANNED_TRIP_ID)
                .with(withDriverCookie(BUS_ID)))
                .andExpect(status().isNoContent());

        // Paso 2: Verificar que ya no aparece en los planificados de hoy
        mockMvc.perform(get("/tracking/trips/today")
                .with(withDriverCookie(BUS_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(not(hasItem(PLANNED_TRIP_ID.toString()))));
    }

    // =========================================================
    // POST /tracking/trips/{tripId}/start - Trip Not Found
    // =========================================================

    @Test
    void shouldReturn404_WhenStartingNonExistentTrip() throws Exception {
        mockMvc.perform(post("/tracking/trips/{tripId}/start", NON_EXISTENT_TRIP_ID)
                .with(withDriverCookie(BUS_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TRIP_NOT_FOUND"));
    }

    // =========================================================
    // POST /tracking/trips/{tripId}/start - Already Started
    // =========================================================

    @Test
    void shouldReturn422_WhenStartingAlreadyInProgressTrip() throws Exception {
        // First start succeeds
        mockMvc.perform(post("/tracking/trips/{tripId}/start", PLANNED_TRIP_ID)
                .with(withDriverCookie(BUS_ID)))
                .andExpect(status().isNoContent());

        // Second attempt on the same trip fails with business rule violation
        mockMvc.perform(post("/tracking/trips/{tripId}/start", PLANNED_TRIP_ID)
                .with(withDriverCookie(BUS_ID)))
                .andExpect(status().is(422));
    }

    // =========================================================
    // GET /tracking/trips/active — PENDING ENDPOINT
    // =========================================================
    // TODO: Uncomment when the endpoint is implemented
    // @Test
    // void shouldReturnStartedTripFromActiveEndpoint() throws Exception {
    // mockMvc.perform(post("/tracking/trips/{tripId}/start", PLANNED_TRIP_ID))
    // .andExpect(status().isOk());
    //
    // mockMvc.perform(get("/tracking/trips/active"))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.id").value(PLANNED_TRIP_ID.toString()))
    // .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    // }
}
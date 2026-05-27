package com.bustracking.tracking.integration.flow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.testinfrastructure.FlowIntegrationTest;

/**
 * Flow Test: Driver Today Trips
 *
 * Business story:
 * "A driver opens the app and sees the planned trips for today"
 *
 * Flow:
 * GET /tracking/trips/today → returns list of PLANNED trips for the bus
 *
 * Covers: HU-17 (obtener viajes del día del bus autenticado)
 */
@Sql(scripts = {
    FlowIntegrationTest.CLEANUP,
    FlowIntegrationTest.BASE,
    FlowIntegrationTest.TRIPS
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DriverTodayTripsFlowTest extends FlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Fixed UUID from tracking-trips.sql — bus with 6 planned trips today
    //private static final UUID BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");

    // =========================================================
    // GET /tracking/trips/today - Driver sees planned trips
    // =========================================================

    @Test
    void shouldReturnTodayPlannedTrips_WhenBusHasRoutesAssigned() throws Exception {
        mockMvc.perform(get("/tracking/trips/today"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].routeName").value("Cartago-Orosi"))
            .andExpect(jsonPath("$[0].origin").value("Cartago"))
            .andExpect(jsonPath("$[0].destination").value("Orosi"))
            .andExpect(jsonPath("$[0].status").value("PLANNED"))
            .andExpect(jsonPath("$[0].departureTime").exists());
    }

    /*
        This test is commented out because the current implementation of the controller has a hardcoded busId, 
        so it always returns the same trips regardless of the test data. Once we implement HU-18 
        (authentication and extracting busId from JWT), we can re-enable this test to validate the empty list 
        scenario for buses without assigned routes.
    @Test
    void shouldReturnEmptyList_WhenBusHasNoRoutesAssigned() throws Exception {
        // BUS_ID_2 exists in tracking-base.sql but has no trips assigned
        // The hardcoded UUID in the controller returns empty for this bus
        // Note: this test validates the empty list response shape
        mockMvc.perform(get("/tracking/trips/today"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
    */

}
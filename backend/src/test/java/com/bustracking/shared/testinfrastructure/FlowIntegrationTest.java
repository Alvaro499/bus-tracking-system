package com.bustracking.shared.testinfrastructure;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @SpringBootTest can not configure MockMvc by default. This annotation tell it to auto-configure MockMvc
 */


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class FlowIntegrationTest extends PostgresTestContainer {

    protected static final String CLEANUP = "/test-data/cleanup.sql";
    protected static final String BASE = "/test-data/tracking-base.sql";
    protected static final String TRIPS = "/test-data/tracking-trips.sql";
}
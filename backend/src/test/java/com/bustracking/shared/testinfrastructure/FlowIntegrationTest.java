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


}
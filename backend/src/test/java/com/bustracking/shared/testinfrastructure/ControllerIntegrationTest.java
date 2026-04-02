package com.bustracking.shared.testinfrastructure;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;

// Base for tests of CONTROLLER (HTTP + Spring, mocked DB)
@WebMvcTest
@ActiveProfiles("test")
public abstract class ControllerIntegrationTest {

    // It doesn't need TestContainers because we mock the service layer
}

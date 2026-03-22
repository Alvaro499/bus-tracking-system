package com.bustracking.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

/**
 * Integration Test Example: UserController
 *
 * Extends IntegrationTest to get TestContainers + Spring context automatically.
 * Tests complete flow: Controller -> Service -> Repository -> Real Database
 */
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // TODO: Setup test data via repositories
    }

    @Test
    @DisplayName("Should create user via POST endpoint")
    void testCreateUser_ValidData_ReturnsCreated() {
        // TODO: Arrange (create request object)

        // TODO: Act (call endpoint via TestRestTemplate)

        // TODO: Assert (verify response status and data in real database)
    }

    @Test
    @DisplayName("Should retrieve user via GET endpoint")
    void testGetUser_UserExists_ReturnsOk() {
        // TODO: Arrange (setup user in database)

        // TODO: Act (call GET endpoint)

        // TODO: Assert (verify response)
    }
}

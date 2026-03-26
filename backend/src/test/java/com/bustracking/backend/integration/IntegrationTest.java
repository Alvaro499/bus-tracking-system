package com.bustracking.backend.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bustracking.backend.config.IntegrationTestConfig;

/**
 * Base class for Integration Tests
 *
 * All integration tests should extend this class to avoid repeating TestContainers setup.
 * Provides:
 * - Real PostgreSQL database via TestContainers
 * - Automatic Spring context loading
 * - Random port for REST tests
 *
 * Usage:
 * class MyIntegrationTest extends IntegrationTest {
 *     @Test
 *     void testSomething() { ... }
 * }
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")   // Use application-test.properties for tests
@Import(IntegrationTestConfig.class)
public abstract class IntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bustracking_db_test")
            .withUsername("bustracking_test_user")
            .withPassword("test_password_random");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}

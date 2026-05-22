package com.bustracking.shared.testinfrastructure;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;


/**
 1. Testcontainers spin up and start PostgreSQL Docker container
   
2. spring.sql.init.schema-locations loads  init-test.sql
    CREATE TABLE company, bus, bus_location, etc.
   
3. Every Test starts and use :
   * @Sql loads fixtures-shared.sql
   * @Sql loads tracking-fixtures.sql
   * INSERT company, buses
   
4. Test runs with clean data
   
5. Cleanup of data after each kind of test

    5.1 @DataJpaTest rolls back transactions after each test method automatically
    5.2 @SpringBootTest use cleanup.sql to truncate tables before each test method (configured in @Sql annotation)
    5.3 @DataJpaTest also uses cleanup.sql before each test method in order to avoid cross-test pollution from FlowTests
 */


public abstract class PostgresTestContainer {

    public static final PostgreSQLContainer<?> POSTGRES = 
        new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("bustracking_db_test")
                .withUsername("bustracking_test_user")
                .withPassword("test_password_random")
                .withReuse(true);

    // Bloque estático: la JVM garantiza que se ejecutará solo una vez
    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
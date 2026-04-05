package com.bustracking.shared.testinfrastructure;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 1. Testcontainers spin up and start PostgreSQL Docker container
   
2. spring.sql.init.schema-locations loads  init-test.sql
    CREATE TABLE company, bus, bus_location, etc.
   
3. Test starts:
   * @Sql loads fixtures-shared.sql
   * @Sql loads tracking-fixtures.sql
   * INSERT company, buses
   
4. Test runs with clean data
   
5. Automatically ROLLBACK
 */


/* 

@DataJpaTest: starts only the persistence layer, including JPA, repositories, and transactions. No controllers, no security, and no services.
@DataJpaTest + @Testcontainers: activates the Spring Context using a real database.

@Import(BackendApplication.class):
    - Ensures the BackendApplication configuration is loaded (it finds @SpringBootConfiguration).

*/

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryIntegrationTest {

    //static so there will be only one docker container for all tests
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

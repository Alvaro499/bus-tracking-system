package com.bustracking.shared.testinfrastructure;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class PostgresTestContainer {

    // 1. Quita @Container y haz el contenedor estático y público
    // 2. Usa DockerImageName para mayor claridad
    // 3. Activa .withReuse(true) para que en local no se reinicie entre ejecuciones
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
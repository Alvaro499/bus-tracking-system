package com.bustracking.shared.testinfrastructure;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/*
 * RedisContainer starts a Docker container that internally runs Redis on port 6379.
 *
 * However, Docker maps that internal port to a random port on the host (for example, 32768). 
 * This avoids conflicts if you already have another Redis running.
 *
 * REDIS.getMappedPort(6379) returns exactly that host port (the one that Docker assigned).
 *
 * Spring Boot needs to connect to Redis through the host port, not the internal container port.
 *
 * If we did not inject that dynamic port, Spring Boot would try to connect to localhost:6379, 
 * which is not the correct port (the connection would fail).
 */

public interface RedisTestContainer {

    GenericContainer<?> REDIS = new GenericContainer<>(
            DockerImageName.parse("redis:8-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    // Interface Initialization Static Block : it does execute only once
    static void start() {
        if (!REDIS.isRunning()) {
            REDIS.start();
        }
    }

    /*
     * Workflow execution flow:
     * * 1. Testcontainers starts the container: At test startup, Testcontainers
     * requests Docker
     * to create the Redis container. Docker creates it and assigns a random free
     * port
     * from the host machine (e.g., 54231).
     * * 2. The dynamic method triggers: This method (typically annotated
     * with @DynamicPropertySource)
     * execves immediately after the container is up and running, but before Spring
     * Boot
     * finishes its configuration.
     * * 3. Injection into Spring's memory: The method captures that random port
     * (54231) and
     * registers it directly into the Spring Environment in memory, temporarily
     * overriding
     * the 'spring.data.redis.port' property.
     * * 4. Spring Boot connects successfully: When Spring Boot looks for the Redis
     * port to establish
     * the connection, it reads the value registered in memory (54231) instead of
     * looking for
     * a physical .properties file.
     */
    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        start();
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

}

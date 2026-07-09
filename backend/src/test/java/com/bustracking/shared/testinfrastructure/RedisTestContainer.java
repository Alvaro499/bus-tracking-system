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
            .withReuse(true);

    // Interface Initialization Static Block : it does execute only once
    static void start() {
        if (!REDIS.isRunning()) {
            REDIS.start();
        }
    }

    // We tell Spring to call this method before initializing the
    // ApplicationContext, so it is possible to register this docker properties
    // whose values are unknown until runtime.
    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        start();
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

}

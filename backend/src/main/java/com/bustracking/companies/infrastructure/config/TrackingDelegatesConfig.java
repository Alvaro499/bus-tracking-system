package com.bustracking.companies.infrastructure.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bustracking.companies.domain.repository.BusRepository;
import com.bustracking.tracking.domain.contract.BusExistsById;

/**
 * Configuration for Tracking Module Delegates
 * 
 * This class is responsible for wiring the delegates (functional contracts)
 * that the tracking module requires. It acts as a bridge between modules:
 * - Companies provides the implementation (via Spring Data repositories)
 * - Tracking receives the behavior as a functional interface
 * 
 * All cross-module delegation is centralized here, making dependencies explicit
 * and preventing tight coupling.
 * 
 * By using delegates instead of full repository interfaces, we:
 * - Keep contracts minimal (single responsibility)
 * - Prevent garbage method accumulation in interfaces
 * - Make dependencies clear (tracking needs "does bus exist?", nothing more)
 */
@Configuration
public class TrackingDelegatesConfig {

    private final BusRepository busRepository;

    public TrackingDelegatesConfig(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    /**
     * Provides a delegate for checking if a bus exists
     * 
     * This bean is injected into the tracking module's use cases.
     * Companies module keeps full control of the logic (is the bus active?
     * does it belong to an active company? etc).
     * Tracking module only knows "bus exists or not".
     * 
     * @return a function that checks bus existence by ID
     */
    @Bean
    public BusExistsById busExistsById() {
        // Method reference: delegates to the repository's existsById method
        // 
        // This is equivalent to:
        // busId -> busRepository.existsById(busId)
        // 
        // But using method reference syntax is cleaner and more functional.
        // The repository is already autowired, so this returns a closure
        // that captures the repository instance.
        return busRepository::existsById;
    }
}


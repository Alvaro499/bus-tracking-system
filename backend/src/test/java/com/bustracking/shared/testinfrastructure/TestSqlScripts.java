package com.bustracking.shared.testinfrastructure;

/**
     * Centralized SQL script paths for integration and flow tests.
     * 
     * Usage in subclasses:
     * <pre>
     * {@code @Sql(scripts = {CLEANUP, BASE, TRIPS})}
     * </pre>
     * Static imports keep test annotations clean and maintainable.

*/
public final class TestSqlScripts {

    private TestSqlScripts() {
        // Prevent instantiation
    }
    public static final String CLEANUP = "/test-data/cleanup.sql";
    public static final String BASE = "/test-data/tracking-base.sql";
    public static final String TRIPS = "/test-data/tracking-trips.sql";
} 

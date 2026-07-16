package com.bustracking.shared.testinfrastructure;

/**
 * Centralized SQL script paths for integration and flow tests.
 * 
 * Usage in subclasses:
 * 
 * <pre>
 * {@code @Sql(scripts = {CLEANUP, BASE, TRIPS})}
 * </pre>
 * 
 * Static imports keep test annotations clean and maintainable.
 * 
 */
public final class TestSqlScripts {

    private TestSqlScripts() {
        // Prevent instantiation
    }

    //common
    public static final String CLEANUP = "/test-data/common/cleanup.sql";
    public static final String BASE = "/test-data/common/tracking-base.sql";
 
    //trip
    public static final String TRIP_COMMON = "/test-data/repository/trip/trip-common.sql";
    public static final String PLANNED_TRIPS = "/test-data/repository/trip/planned-trips.sql";
    public static final String TRIP_FILTER_DATA = "/test-data/repository/trip/trip-filter-data.sql";
    public static final String TRIP_DETAIL = "/test-data/repository/trip/trip-detail.sql";
    public static final String BUS_CREDENTIALS = "/test-data/repository/trip/bus-credentials.sql";

}

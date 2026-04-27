-- ============================================================================
-- INITIALIZATION: Development Database
-- ============================================================================
-- Executed only on first Docker startup (when volume is empty)
-- Creates all required tables. If data exists, this does not run.
-- Reset: docker-compose down -v && docker-compose up -d
-- ============================================================================



-- ============================================================================
-- SCHEMAS
-- ============================================================================

CREATE SCHEMA admin;
CREATE SCHEMA companies;
CREATE SCHEMA tracking;

CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ============================================================================
-- SCHEMA: admin (USERS ONLY - no FK to companies yet)
-- Owns platform governance: users, company approval requests, audit trail
-- ============================================================================

CREATE TABLE admin."user" (
    id UUID PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(12) NOT NULL,
    global_role VARCHAR(20) NOT NULL CHECK (global_role IN ('PLATFORM_ADMIN', 'COMPANY_USER')),
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- SCHEMA: companies
-- Owns company operations: fleet, routes, stops, schedules and trips
-- MUST be created before admin.company_request (which references it)
-- ============================================================================

CREATE TABLE companies.company (
    id UUID PRIMARY KEY,
    tax_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE companies.company_user (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES admin."user"(id) ON DELETE RESTRICT,
    company_id UUID NOT NULL REFERENCES companies.company(id) ON DELETE RESTRICT,
    role VARCHAR(20) NOT NULL CHECK (role IN ('OWNER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, company_id)
);

CREATE TABLE companies.route (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies.company(id) ON DELETE RESTRICT,
    name VARCHAR(150) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    origin VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    flat_fare BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE companies.stop (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies.company(id) ON DELETE RESTRICT,
    name VARCHAR(150) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    reference TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE companies.route_stop (
    id UUID PRIMARY KEY,
    route_id UUID NOT NULL REFERENCES companies.route(id) ON DELETE RESTRICT,
    stop_id UUID NOT NULL REFERENCES companies.stop(id) ON DELETE RESTRICT,
    order_index INTEGER NOT NULL,
    estimated_time_offset INTEGER NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (route_id, order_index)
);

CREATE TABLE companies.route_stop_fare (
    id UUID PRIMARY KEY,
    route_stop_id UUID NOT NULL REFERENCES companies.route_stop(id) ON DELETE RESTRICT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE companies.bus (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies.company(id) ON DELETE RESTRICT,
    plate VARCHAR(20) NOT NULL UNIQUE,
    internal_number VARCHAR(20),
    has_ramp BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE')),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE companies.schedule (
    id UUID PRIMARY KEY,
    route_id UUID NOT NULL REFERENCES companies.route(id) ON DELETE RESTRICT,
    departure_time TIME NOT NULL,
    day_of_week INTEGER NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_date DATE NOT NULL,
    end_date DATE NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (route_id, day_of_week, departure_time, start_date),
    CHECK (end_date IS NULL OR end_date > start_date)
);

CREATE TABLE companies.trip (
    id UUID PRIMARY KEY,
    schedule_id UUID NOT NULL REFERENCES companies.schedule(id) ON DELETE RESTRICT,
    bus_id UUID NULL REFERENCES companies.bus(id) ON DELETE SET NULL,
    trip_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'RELIEVED')),
    cancellation_reason TEXT NULL,
    actual_start_time TIME NULL,
    actual_end_time TIME NULL,
    delay_minutes INT NULL,
    assigned_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (schedule_id, trip_date)
);

-- ============================================================================
-- SCHEMA: admin (continued - FK to companies)
-- Now companies.company exists, we can create company_request
-- ============================================================================

CREATE TABLE admin.company_request (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies.company(id) ON DELETE CASCADE,
    reviewed_by UUID NULL REFERENCES admin."user"(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    rejection_reason TEXT NULL,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL
);

CREATE TABLE admin.audit_log (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES admin."user"(id) ON DELETE SET NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'ASSIGN', 'REASSIGN')),
    old_values JSONB,
    new_values JSONB,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- SCHEMA: tracking
-- Owns real-time data: bus location and device authentication
-- High write frequency, volatile data, consumed by the bus app
-- ============================================================================

CREATE TABLE tracking.bus_location (
    bus_id UUID PRIMARY KEY,
    lat DECIMAL(9,6) NOT NULL,
    lng DECIMAL(9,6) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tracking.bus_credential (
    id UUID PRIMARY KEY,
    bus_id UUID NOT NULL REFERENCES companies.bus(id) ON DELETE RESTRICT,
    password_hash VARCHAR(255) NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP NULL,
    UNIQUE (bus_id)
);


-- ============================================================================
-- Non-Clustered Indexes for Performance
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Auth & Permission Checks
-- Used when verifying what company a user belongs to and what role they have
-- ----------------------------------------------------------------------------
CREATE INDEX idx_CompanyUser_UserId ON companies.company_user(user_id);
CREATE INDEX idx_CompanyUser_CompanyId ON companies.company_user(company_id);

-- ----------------------------------------------------------------------------
-- Company Admin Panel
-- Used when a company admin manages their own routes, stops and buses
-- Used when the system filters and gives the admin user only the company data
-- ----------------------------------------------------------------------------
CREATE INDEX idx_Route_CompanyId ON companies.route(company_id);
CREATE INDEX idx_Stop_CompanyId ON companies.stop(company_id);
CREATE INDEX idx_Bus_CompanyId ON companies.bus(company_id);

-- ----------------------------------------------------------------------------
-- Public Search (used by end users searching for routes)
-- Origin + destination for exact match, name for partial text search
-- ----------------------------------------------------------------------------
CREATE INDEX idx_Route_OriginDestination ON companies.route(origin, destination);

-- Partial text search on route name (e.g. user types "Cartago")
-- Requires: CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_Route_Name_GIN ON companies.route USING GIN (name gin_trgm_ops);


-- ----------------------------------------------------------------------------
-- Map & Real-Time Tracking (used by end users viewing the map)
-- Core query: find active trips for today to show buses on the map
-- ----------------------------------------------------------------------------
CREATE INDEX idx_Trip_StatusDate ON companies.trip(status, trip_date);
CREATE INDEX idx_Trip_BusId ON companies.trip(bus_id);

-- ----------------------------------------------------------------------------
-- Stop & Route Details (used when loading route stops on the map)
-- ----------------------------------------------------------------------------
CREATE INDEX idx_RouteStop_RouteId ON companies.route_stop(route_id);

-- ----------------------------------------------------------------------------
-- Schedule Management (used by admins and trip generation)
-- ----------------------------------------------------------------------------
CREATE INDEX idx_Schedule_RouteId ON companies.schedule(route_id);

-- ----------------------------------------------------------------------------
-- Trip Management (used by admins and the bus app)
-- Drivers look up trips by schedule when starting their shift
-- ----------------------------------------------------------------------------
CREATE INDEX idx_Trip_ScheduleId ON companies.trip(schedule_id);

-- ----------------------------------------------------------------------------
-- Bus App (used by drivers searching for available trips)
-- Stops lookup by name when admins assign or modify stops
-- ----------------------------------------------------------------------------
CREATE INDEX idx_Stop_Name ON companies.stop(name);

-- ----------------------------------------------------------------------------
-- Fare Lookup (used when showing ticket prices per stop)
-- ----------------------------------------------------------------------------
CREATE INDEX idx_RouteStopFare_RouteStopId ON companies.route_stop_fare(route_stop_id, is_active);
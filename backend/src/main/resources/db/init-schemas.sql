-- ============================================================================
-- INITIALIZATION: Database with Schemas (BDD Structure)
-- ============================================================================
-- This version organizes tables by Bounded Context using PostgreSQL schemas
-- Executed only on first Docker startup (when volume is empty)
-- Reset: docker-compose down -v && docker-compose up -d
-- ============================================================================

-- ============================================================================
-- 1. CREATE SCHEMAS
-- ============================================================================
CREATE SCHEMA IF NOT EXISTS admin;
CREATE SCHEMA IF NOT EXISTS companies;
CREATE SCHEMA IF NOT EXISTS tracking;
CREATE SCHEMA IF NOT EXISTS drivers;

COMMENT ON SCHEMA admin IS 'Governance & Platform Administration (Users, Companies, Audit)';
COMMENT ON SCHEMA companies IS 'Company Operational Data (Routes, Buses, Schedules, Trips)';
COMMENT ON SCHEMA tracking IS 'Real-time Tracking Data (GPS Coordinates, Events)';
COMMENT ON SCHEMA drivers IS 'Driver Management (Future: Authentication, Sessions)';

-- ============================================================================
-- 2. SCHEMA: ADMIN
-- ============================================================================
-- Governance: User management, company approvals, audit logs

CREATE TABLE admin."user" (
    id UUID PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(12) NOT NULL,
    global_role VARCHAR(20) NOT NULL CHECK (global_role IN ('PLATFORM_ADMIN', 'COMPANY_USER')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admin.company (
    id UUID PRIMARY KEY,
    tax_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admin.company_user (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES admin."user"(id) ON DELETE RESTRICT,
    company_id UUID NOT NULL REFERENCES admin.company(id) ON DELETE RESTRICT,
    role VARCHAR(20) NOT NULL CHECK (role IN ('OWNER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, company_id)
);

CREATE TABLE admin.company_request (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES admin.company(id) ON DELETE CASCADE,
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

-- Indexes for admin schema
CREATE INDEX idx_admin_CompanyUser_UserId ON admin.company_user(user_id);
CREATE INDEX idx_admin_CompanyUser_CompanyId ON admin.company_user(company_id);
CREATE INDEX idx_admin_AuditLog_UserId ON admin.audit_log(user_id);
CREATE INDEX idx_admin_AuditLog_EntityTypeId ON admin.audit_log(entity_type, entity_id);
CREATE INDEX idx_admin_AuditLog_OccurredAt ON admin.audit_log(occurred_at);

-- ============================================================================
-- 3. SCHEMA: COMPANIES
-- ============================================================================
-- Operational data: Routes, Buses, Schedules, Trips (the main domain)

CREATE TABLE companies.route (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES admin.company(id) ON DELETE RESTRICT,
    name VARCHAR(150) NOT NULL,
    origin VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    flat_fare BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE companies.stop (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES admin.company(id) ON DELETE RESTRICT,
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

CREATE TABLE companies.bus (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES admin.company(id) ON DELETE RESTRICT,
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
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
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
    status VARCHAR(20) NOT NULL CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'REASSIGNED')),
    actual_start_time TIME NULL,
    actual_end_time TIME NULL,
    delay_minutes INT NULL,
    assigned_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (schedule_id, trip_date)
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

-- Indexes for companies schema
CREATE INDEX idx_companies_Route_CompanyId ON companies.route(company_id);
CREATE INDEX idx_companies_Route_Name ON companies.route(name);
CREATE INDEX idx_companies_Stop_CompanyId ON companies.stop(company_id);
CREATE INDEX idx_companies_Bus_CompanyId ON companies.bus(company_id);
CREATE INDEX idx_companies_Trip_StatusDate ON companies.trip(status, trip_date);
CREATE INDEX idx_companies_Trip_ScheduleId ON companies.trip(schedule_id);

-- ============================================================================
-- 4. SCHEMA: TRACKING
-- ============================================================================
-- Real-time data: Bus locations (GPS), Events, Updates from drivers

CREATE TABLE tracking.bus_location (
    bus_id UUID PRIMARY KEY REFERENCES companies.bus(id) ON DELETE CASCADE,
    lat DECIMAL(9,6) NOT NULL,
    lng DECIMAL(9,6) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Future: Table to store GPS history (optional, for analytics)
-- CREATE TABLE tracking.bus_location_history (
--     id UUID PRIMARY KEY,
--     bus_id UUID NOT NULL REFERENCES companies.bus(id) ON DELETE CASCADE,
--     trip_id UUID REFERENCES companies.trip(id) ON DELETE SET NULL,
--     lat DECIMAL(9,6) NOT NULL,
--     lng DECIMAL(9,6) NOT NULL,
--     accuracy FLOAT,
--     speed FLOAT,
--     timestamp TIMESTAMP NOT NULL
-- );

-- Future: Tracking events (bus reached stop, trip started, etc)
-- CREATE TABLE tracking.tracking_event (
--     id UUID PRIMARY KEY,
--     trip_id UUID NOT NULL REFERENCES companies.trip(id) ON DELETE CASCADE,
--     event_type VARCHAR(50) NOT NULL,
--     event_data JSONB,
--     timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );

-- Indexes for tracking schema
CREATE INDEX idx_tracking_BusLocation_UpdatedAt ON tracking.bus_location(updated_at);

-- ============================================================================
-- 5. SCHEMA: DRIVERS (Future)
-- ============================================================================
-- Driver management: Authentication, Sessions, Assignments

-- Future: Driver entity
-- CREATE TABLE drivers.driver (
--     id UUID PRIMARY KEY,
--     company_id UUID NOT NULL REFERENCES admin.company(id) ON DELETE RESTRICT,
--     document_id VARCHAR(20) NOT NULL,
--     name VARCHAR(255) NOT NULL,
--     phone VARCHAR(20),
--     status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     UNIQUE (company_id, document_id)
-- );

-- Future: Driver sessions
-- CREATE TABLE drivers.driver_session (
--     id UUID PRIMARY KEY,
--     driver_id UUID NOT NULL REFERENCES drivers.driver(id) ON DELETE RESTRICT,
--     trip_id UUID NOT NULL REFERENCES companies.trip(id) ON DELETE RESTRICT,
--     login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     logout_time TIMESTAMP NULL,
--     is_active BOOLEAN DEFAULT TRUE
-- );

-- ============================================================================
-- SUMMARY
-- ============================================================================
-- admin:       5 tables  (Users, Companies, Audit)
-- companies:   7 tables  (Routes, Buses, Schedules, Trips)
-- tracking:    1 table   (GPS Locations)
-- drivers:     0 tables  (Reserved for future)
--
-- Total: 13 tables organized by Bounded Context
-- ============================================================================

-- ============================================================================
-- INITIALIZATION: Development Database
-- ============================================================================
-- Executed only on first Docker startup (when volume is empty)
-- Creates all required tables. If data exists, this does not run.
-- Reset: docker-compose down -v && docker-compose up -d
-- ============================================================================

CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(12) NOT NULL,
    global_role VARCHAR(20) NOT NULL CHECK (global_role IN ('PLATFORM_ADMIN', 'COMPANY_USER')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE company(
	id UUID PRIMARY KEY,
	tax_id VARCHAR(20) UNIQUE NOT NULL,
	name VARCHAR(255) NOT NULL,
	email VARCHAR(150) UNIQUE NOT NULL,
	phone  VARCHAR(20) UNIQUE NOT NULL,
	status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE company_user(
	id UUID PRIMARY KEY,
	user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE RESTRICT,
	company_id UUID NOT NULL REFERENCES company(id) ON DELETE RESTRICT,
	role VARCHAR(20) NOT NULL CHECK (role IN ('OWNER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UNIQUE (user_id, company_id)
);

CREATE TABLE company_request(
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES company(id) ON DELETE CASCADE,
    reviewed_by UUID NULL REFERENCES "user"(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
	rejection_reason TEXT NULL,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL
);

CREATE TABLE audit_log(
	id UUID PRIMARY KEY,
	user_id UUID REFERENCES "user"(id) ON DELETE SET NULL,
	entity_type VARCHAR(50) NOT NULL,
	entity_id UUID NOT NULL,
	action VARCHAR(50) NOT NULL CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'ASSIGN', 'REASSIGN')),
    old_values JSONB,
    new_values JSONB,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE route (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES company(id) ON DELETE RESTRICT,
    name VARCHAR(150) NOT NULL,
    origin VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    flat_fare BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stop (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES company(id) ON DELETE RESTRICT,
    name VARCHAR(150) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    reference TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE route_stop (
    id UUID PRIMARY KEY,
    route_id UUID NOT NULL REFERENCES route(id) ON DELETE RESTRICT,
    stop_id UUID NOT NULL REFERENCES stop(id) ON DELETE RESTRICT,
    order_index INTEGER NOT NULL,
    estimated_time_offset INTEGER NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (route_id, order_index)
);

CREATE TABLE bus (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES company(id) ON DELETE RESTRICT,
    plate VARCHAR(20) NOT NULL UNIQUE,
    internal_number VARCHAR(20),
    has_ramp BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bus_location (
    bus_id UUID PRIMARY KEY REFERENCES bus(id) ON DELETE CASCADE,
    lat DECIMAL(9,6) NOT NULL,
    lng DECIMAL(9,6) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE schedule (
    id UUID PRIMARY KEY,
    route_id UUID NOT NULL REFERENCES route(id) ON DELETE RESTRICT,
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

CREATE TABLE trip (
    id UUID PRIMARY KEY,
    schedule_id UUID NOT NULL REFERENCES schedule(id) ON DELETE RESTRICT,
    bus_id UUID NULL REFERENCES bus(id) ON DELETE SET NULL,
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

CREATE TABLE route_stop_fare (
    id UUID PRIMARY KEY,
    route_stop_id UUID NOT NULL REFERENCES route_stop(id) ON DELETE RESTRICT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (end_date IS NULL OR end_date >= start_date)
);

-- ============================================================================
-- Non-Clustered Indexes for Performance
-- ============================================================================
CREATE INDEX idx_CompanyUser_UserId ON company_user(user_id);
CREATE INDEX idx_CompanyUser_CompanyId ON company_user(company_id);
CREATE INDEX idx_Route_CompanyId ON route(company_id);
CREATE INDEX idx_Route_Name ON route(name);
CREATE INDEX idx_Stop_CompanyId ON stop(company_id);
CREATE INDEX idx_Bus_CompanyId ON bus(company_id);

-- when user open the app, it will need to know which buses are active for a trip, so index on bus status
-- app will update the system every X secondsto refresh which buses are still active:
CREATE INDEX idx_Trip_StatusDate ON trip(status, trip_date);

-- for management of schedules and trips, and for uers when they want to see a schedule's details
CREATE INDEX idx_Trip_ScheduleId ON trip(schedule_id);
CREATE INDEX idx_AuditLog_UserId ON audit_log(user_id);
CREATE INDEX idx_AuditLog_EntityTypeId ON audit_log(entity_type, entity_id);
CREATE INDEX idx_AuditLog_OccurredAt ON audit_log(occurred_at);

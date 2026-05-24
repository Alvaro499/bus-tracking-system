-- ============================================================================
-- GLOBAL CLEANUP
-- ============================================================================
-- Deletes data in dependency order to avoid FK violations
-- Execute before base seeds and HU seeds
-- ============================================================================

-- ----------------------------------------------------------------------------
-- tracking
-- ----------------------------------------------------------------------------
DELETE FROM tracking.bus_location;
DELETE FROM tracking.bus_credential;

-- ----------------------------------------------------------------------------
-- companies (deepest dependencies first)
-- ----------------------------------------------------------------------------
DELETE FROM companies.trip;

DELETE FROM companies.schedule;

DELETE FROM companies.route_stop_fare;

DELETE FROM companies.route_stop;

DELETE FROM companies.bus_route;

DELETE FROM companies.stop;

DELETE FROM companies.route;

DELETE FROM companies.bus;

DELETE FROM companies.company_user;

-- ----------------------------------------------------------------------------
-- admin
-- ----------------------------------------------------------------------------
DELETE FROM admin.company_request;

DELETE FROM admin.audit_log;

-- ----------------------------------------------------------------------------
-- root entities
-- ----------------------------------------------------------------------------
DELETE FROM companies.company;

DELETE FROM admin."user";
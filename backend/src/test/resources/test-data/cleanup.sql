-- ============================================================================
-- CLEANUP for FlowIntegrationTest
-- Ejecutado antes de cada test para dejar la BD limpia
-- ============================================================================

-- Desactivar triggers de FK temporalmente para evitar errores de orden
-- (alternativa más limpia que ordenar manualmente)
SET session_replication_role = 'replica';

-- Limpiar tablas del módulo tracking (son las de escritura más frecuente)
TRUNCATE TABLE tracking.bus_location CASCADE;
TRUNCATE TABLE tracking.bus_credential CASCADE;

-- Limpiar tablas del módulo companies (orden inverso a dependencias)
TRUNCATE TABLE companies.trip CASCADE;
TRUNCATE TABLE companies.schedule CASCADE;
TRUNCATE TABLE companies.route_stop_fare CASCADE;
TRUNCATE TABLE companies.route_stop CASCADE;
TRUNCATE TABLE companies.stop CASCADE;
TRUNCATE TABLE companies.bus_route CASCADE;
TRUNCATE TABLE companies.route CASCADE;
TRUNCATE TABLE companies.bus CASCADE;
TRUNCATE TABLE companies.company_user CASCADE;
TRUNCATE TABLE companies.company CASCADE;

-- Limpiar tablas de admin que tengan datos (si se usan en tests)
TRUNCATE TABLE admin.audit_log CASCADE;
TRUNCATE TABLE admin.company_request CASCADE;
TRUNCATE TABLE admin."user" CASCADE;

-- Restaurar triggers
SET session_replication_role = 'origin';
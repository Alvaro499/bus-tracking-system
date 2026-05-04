-- ============================================================================
-- HU-17: Gestionar y transmitir el viaje en tiempo real desde la app del bus
-- ============================================================================
-- Seeds para testing de endpoints:
-- - GET /buses/{id}/location (obtener última ubicación)
-- - PUT/POST /buses/{id}/location (recibir coordenadas)
-- Dependencias: 00-base.sql (necesita empresa y bus existentes)
-- ============================================================================

-- Clean up location data
DELETE FROM tracking.bus_location WHERE bus_id = '650e8400-e29b-41d4-a716-446655440001';

-- Insert route: Cartago → Orosi
INSERT INTO companies.route (id, company_id, name, price, origin, destination, flat_fare, is_active, created_at, updated_at)
VALUES ('750e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 'Cartago-Orosi', 1500.00, 'Cartago', 'Orosi', true, true, NOW(), NOW());

-- Insert test location for bus
INSERT INTO tracking.bus_location (bus_id, lat, lng, updated_at)
VALUES ('650e8400-e29b-41d4-a716-446655440001', 9.934739, -84.087502, NOW());

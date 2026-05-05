-- ============================================================================
-- HU-17: Gestionar y transmitir el viaje en tiempo real desde la app del bus
-- ============================================================================
-- Seeds para testing de endpoints:
-- - GET /buses/{id}/location (obtener última ubicación)
-- - PUT/POST /buses/{id}/location (recibir coordenadas)
-- Dependencias: 00-base.sql (necesita empresa y bus existentes)
-- ============================================================================

-- Clean up existing data
DELETE FROM companies.trip WHERE schedule_id IN (SELECT id FROM companies.schedule WHERE route_id = '750e8400-e29b-41d4-a716-446655440000');
DELETE FROM companies.schedule WHERE route_id = '750e8400-e29b-41d4-a716-446655440000';
DELETE FROM companies.route_stop WHERE route_id = '750e8400-e29b-41d4-a716-446655440000';
DELETE FROM companies.stop WHERE company_id = '550e8400-e29b-41d4-a716-446655440000' AND name IN ('Cartago', 'Colegio Vicente de Costa Rica', 'Estado de Paraiso', 'Paraiso Centro', 'Recinto UCR Paraiso', 'Orosi');
DELETE FROM tracking.bus_location WHERE bus_id = '650e8400-e29b-41d4-a716-446655440001';
DELETE FROM companies.route WHERE id = '750e8400-e29b-41d4-a716-446655440000';

-- Insert route: Cartago → Orosi
INSERT INTO companies.route (id, company_id, name, price, origin, destination, flat_fare, is_active, created_at, updated_at)
VALUES ('750e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 'Cartago-Orosi', 1500.00, 'Cartago', 'Orosi', true, true, NOW(), NOW());

-- Insert stops
INSERT INTO companies.stop (id, company_id, name, latitude, longitude, reference, created_at, updated_at)
VALUES 
    ('860e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'Cartago', 9.8612, -83.9180, 'Terminal Central Cartago', NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', 'Colegio Vicente de Costa Rica', 9.8615, -83.9145, 'Frente Colegio Vicente de Costa Rica', NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', 'Estado de Paraiso', 9.8142, -83.8156, 'Estadio de Paraiso', NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440000', 'Paraiso Centro', 9.8098, -83.8042, 'Centro de Paraiso', NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440000', 'Recinto UCR Paraiso', 9.7856, -83.7834, 'Recinto de la UCR en Paraiso', NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440000', 'Orosi', 9.7798, -83.7345, 'Terminal Orosi', NOW(), NOW());

-- Link stops to route in order
INSERT INTO companies.route_stop (id, route_id, stop_id, order_index, estimated_time_offset, created_at)
VALUES 
    ('960e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440001', 1, 0, NOW()),
    ('960e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440002', 2, 5, NOW()),
    ('960e8400-e29b-41d4-a716-446655440003', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440003', 3, 15, NOW()),
    ('960e8400-e29b-41d4-a716-446655440004', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440004', 4, 20, NOW()),
    ('960e8400-e29b-41d4-a716-446655440005', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440005', 5, 30, NOW()),
    ('960e8400-e29b-41d4-a716-446655440006', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440006', 6, 40, NOW());

-- Insert schedules: 8am to 1pm, every hour (all weekdays 1-5, Mon-Fri)
INSERT INTO companies.schedule (id, route_id, departure_time, day_of_week, start_date, end_date, is_active, created_at, updated_at)
VALUES 
    ('a70e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440000', '08:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440000', '09:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440003', '750e8400-e29b-41d4-a716-446655440000', '10:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440004', '750e8400-e29b-41d4-a716-446655440000', '11:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440005', '750e8400-e29b-41d4-a716-446655440000', '12:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440006', '750e8400-e29b-41d4-a716-446655440000', '13:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW());

-- Insert test location for bus
INSERT INTO tracking.bus_location (bus_id, lat, lng, updated_at)
VALUES ('650e8400-e29b-41d4-a716-446655440001', 9.934739, -84.087502, NOW());

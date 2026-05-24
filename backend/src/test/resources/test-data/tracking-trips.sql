-- ============================================================================
-- Tracking Trip Fixtures: Route, Stops, Schedules, Trips
-- ============================================================================
-- Required by: TripRepositoryTest, DriverTripQueryFlowTest
-- Depends on: tracking-base.sql (company + buses)
-- ============================================================================


-- ============================================================================
-- BASE ROUTE DATA
-- Used by: all trip tests
-- ============================================================================

INSERT INTO companies.route (id, company_id, name, price, origin, destination, flat_fare, is_active, created_at, updated_at)
VALUES ('750e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 'Cartago-Orosi', 1500.00, 'Cartago', 'Orosi', true, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO companies.stop (id, company_id, name, latitude, longitude, reference, created_at, updated_at)
VALUES 
    ('860e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'Cartago',             9.8612, -83.9180, 'Terminal Central Cartago',            NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440000', 'Colegio Vicente',     9.8615, -83.9145, 'Frente Colegio Vicente de Costa Rica', NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', 'Estado de Paraiso',   9.8142, -83.8156, 'Estadio de Paraiso',                  NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440000', 'Paraiso Centro',      9.8098, -83.8042, 'Centro de Paraiso',                   NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440000', 'Recinto UCR Paraiso', 9.7856, -83.7834, 'Recinto de la UCR en Paraiso',        NOW(), NOW()),
    ('860e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440000', 'Orosi',               9.7798, -83.7345, 'Terminal Orosi',                      NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO companies.route_stop (id, route_id, stop_id, order_index, estimated_time_offset, created_at)
VALUES 
    ('960e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440001', 1,  0, NOW()),
    ('960e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440002', 2,  5, NOW()),
    ('960e8400-e29b-41d4-a716-446655440003', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440003', 3, 15, NOW()),
    ('960e8400-e29b-41d4-a716-446655440004', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440004', 4, 20, NOW()),
    ('960e8400-e29b-41d4-a716-446655440005', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440005', 5, 30, NOW()),
    ('960e8400-e29b-41d4-a716-446655440006', '750e8400-e29b-41d4-a716-446655440000', '860e8400-e29b-41d4-a716-446655440006', 6, 40, NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO companies.bus_route (id, bus_id, route_id, created_at)
VALUES ('c70e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440000', NOW())
ON CONFLICT (id) DO NOTHING;


-- ============================================================================
-- PLANNED TRIPS — one per day of week (7 schedules + 7 trips)
-- Used by: shouldReturnPlannedTripsForTodayWhenBusHasRoutes
--          DriverTripQueryFlowTest
-- ============================================================================

INSERT INTO companies.schedule (id, route_id, departure_time, day_of_week, start_date, end_date, is_active, created_at, updated_at)
VALUES 
    ('a70e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440000', '08:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440000', '09:00:00', 2, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440003', '750e8400-e29b-41d4-a716-446655440000', '10:00:00', 3, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440004', '750e8400-e29b-41d4-a716-446655440000', '11:00:00', 4, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440005', '750e8400-e29b-41d4-a716-446655440000', '12:00:00', 5, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440006', '750e8400-e29b-41d4-a716-446655440000', '13:00:00', 6, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-446655440007', '750e8400-e29b-41d4-a716-446655440000', '14:00:00', 7, CURRENT_DATE, NULL, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO companies.trip (id, schedule_id, trip_date, bus_id, status, created_at, updated_at)
VALUES 
    ('b70e8400-e29b-41d4-a716-446655440001', 'a70e8400-e29b-41d4-a716-446655440001', CURRENT_DATE, NULL, 'PLANNED', NOW(), NOW()),
    ('b70e8400-e29b-41d4-a716-446655440002', 'a70e8400-e29b-41d4-a716-446655440002', CURRENT_DATE, NULL, 'PLANNED', NOW(), NOW()),
    ('b70e8400-e29b-41d4-a716-446655440003', 'a70e8400-e29b-41d4-a716-446655440003', CURRENT_DATE, NULL, 'PLANNED', NOW(), NOW()),
    ('b70e8400-e29b-41d4-a716-446655440004', 'a70e8400-e29b-41d4-a716-446655440004', CURRENT_DATE, NULL, 'PLANNED', NOW(), NOW()),
    ('b70e8400-e29b-41d4-a716-446655440005', 'a70e8400-e29b-41d4-a716-446655440005', CURRENT_DATE, NULL, 'PLANNED', NOW(), NOW()),
    ('b70e8400-e29b-41d4-a716-446655440006', 'a70e8400-e29b-41d4-a716-446655440006', CURRENT_DATE, NULL, 'PLANNED', NOW(), NOW()),
    ('b70e8400-e29b-41d4-a716-446655440007', 'a70e8400-e29b-41d4-a716-446655440007', CURRENT_DATE, NULL, 'PLANNED', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;


-- ============================================================================
-- FILTER TEST DATA — extra schedules and trips for TripRepositoryTest
-- These verify that the JPQL correctly excludes non-PLANNED and past trips
-- ============================================================================

INSERT INTO companies.schedule (id, route_id, departure_time, day_of_week, start_date, end_date, is_active, created_at, updated_at)
VALUES 
    ('a70e8400-e29b-41d4-a716-000000000001', '750e8400-e29b-41d4-a716-446655440000', '20:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-000000000002', '750e8400-e29b-41d4-a716-446655440000', '21:00:00', 2, CURRENT_DATE, NULL, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- IN_PROGRESS trip — verifies status filter excludes non-PLANNED
INSERT INTO companies.trip (id, schedule_id, trip_date, bus_id, status, created_at, updated_at)
VALUES (
    'b70e8400-e29b-41d4-a716-000000000001',
    'a70e8400-e29b-41d4-a716-000000000001',
    CURRENT_DATE,
    '650e8400-e29b-41d4-a716-446655440001',
    'IN_PROGRESS',
    NOW(), NOW()
) ON CONFLICT (id) DO NOTHING;

-- Yesterday PLANNED trip — verifies date filter excludes past dates
INSERT INTO companies.trip (id, schedule_id, trip_date, bus_id, status, created_at, updated_at)
VALUES (
    'b70e8400-e29b-41d4-a716-000000000002',
    'a70e8400-e29b-41d4-a716-000000000002',
    CURRENT_DATE - INTERVAL '1 day',
    NULL,
    'PLANNED',
    NOW() - INTERVAL '1 day', -- Sincronizado: Se creó ayer
    NOW() - INTERVAL '1 day'  -- Sincronizado: Se actualizó ayer
) ON CONFLICT (id) DO NOTHING;
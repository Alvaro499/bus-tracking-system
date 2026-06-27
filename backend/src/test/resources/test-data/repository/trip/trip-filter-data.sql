-- trip-filter-data.sql
-- Extra data for filter tests: IN_PROGRESS and yesterday trips

INSERT INTO companies.schedule (id, route_id, departure_time, day_of_week, start_date, end_date, is_active, created_at, updated_at)
VALUES 
    ('a70e8400-e29b-41d4-a716-000000000001', '750e8400-e29b-41d4-a716-446655440000', '20:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW()),
    ('a70e8400-e29b-41d4-a716-000000000002', '750e8400-e29b-41d4-a716-446655440000', '21:00:00', 2, CURRENT_DATE, NULL, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- IN_PROGRESS trip
INSERT INTO companies.trip (id, schedule_id, trip_date, bus_id, status, created_at, updated_at)
VALUES (
    'b70e8400-e29b-41d4-a716-000000000001',
    'a70e8400-e29b-41d4-a716-000000000001',
    CURRENT_DATE,
    '650e8400-e29b-41d4-a716-446655440001',
    'IN_PROGRESS',
    NOW(), NOW()
) ON CONFLICT (id) DO NOTHING;

-- Yesterday PLANNED trip
INSERT INTO companies.trip (id, schedule_id, trip_date, bus_id, status, created_at, updated_at)
VALUES (
    'b70e8400-e29b-41d4-a716-000000000002',
    'a70e8400-e29b-41d4-a716-000000000002',
    CURRENT_DATE - INTERVAL '1 day',
    NULL,
    'PLANNED',
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day'
) ON CONFLICT (id) DO NOTHING;
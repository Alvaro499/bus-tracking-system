-- trip-detail.sql
-- Data for findTripScheduleById and findStopsByTripId

-- Un schedule para el trip de detalle
INSERT INTO companies.schedule (id, route_id, departure_time, day_of_week, start_date, end_date, is_active, created_at, updated_at)
VALUES ('a70e8400-e29b-41d4-a716-446655449999', '750e8400-e29b-41d4-a716-446655440000', '15:00:00', 1, CURRENT_DATE, NULL, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- El trip sobre el que consultaremos detalles
INSERT INTO companies.trip (id, schedule_id, trip_date, bus_id, status, created_at, updated_at)
VALUES ('b70e8400-e29b-41d4-a716-446655449999', 'a70e8400-e29b-41d4-a716-446655449999', CURRENT_DATE, '650e8400-e29b-41d4-a716-446655440001', 'IN_PROGRESS', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Trip stops (algunas completadas, otras no)
INSERT INTO companies.trip_stop (id, trip_id, route_stop_id, completed_at) VALUES
    (gen_random_uuid(), 'b70e8400-e29b-41d4-a716-446655449999', '960e8400-e29b-41d4-a716-446655440001', NOW()),                      -- completada
    (gen_random_uuid(), 'b70e8400-e29b-41d4-a716-446655449999', '960e8400-e29b-41d4-a716-446655440002', NULL),                      -- no completada
    (gen_random_uuid(), 'b70e8400-e29b-41d4-a716-446655449999', '960e8400-e29b-41d4-a716-446655440003', NOW() - INTERVAL '10 minutes') -- completada con tiempo anterior
ON CONFLICT (trip_id, route_stop_id) DO NOTHING;
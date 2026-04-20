-- ============================================================================
-- Tracking Module Test Fixtures
-- ============================================================================
-- Tracking-specific data for integration tests
-- Imports shared fixtures (company) automatically via @Sql

-- Insert test buses (references company from fixtures-shared.sql)
INSERT INTO companies.bus (id, company_id, plate, status, created_at, updated_at)
VALUES 
    ('650e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440000'::uuid, 'TEST001', 'ACTIVE', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440002'::uuid, '550e8400-e29b-41d4-a716-446655440000'::uuid, 'TEST002', 'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;

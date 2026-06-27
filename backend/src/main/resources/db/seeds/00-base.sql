-- ============================================================================
-- Tracking Base Fixtures: Company + Buses
-- ============================================================================
INSERT INTO companies.company (id, tax_id, name, email, phone, status, created_at, updated_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440000'::uuid,
    '12-345-678901',
    'Test Shared Company',
    'shared@test.com',
    '88888888',
    'ACTIVE',
    NOW(),
    NOW()
) ON CONFLICT DO NOTHING;

INSERT INTO companies.bus (id, company_id, plate, status, created_at, updated_at)
VALUES 
    ('650e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440000'::uuid, 'TEST001', 'ACTIVE', NOW(), NOW()),
    ('650e8400-e29b-41d4-a716-446655440002'::uuid, '550e8400-e29b-41d4-a716-446655440000'::uuid, 'TEST002', 'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;
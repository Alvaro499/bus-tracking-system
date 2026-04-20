-- ============================================================================
-- Shared Test Fixtures
-- ============================================================================
-- Base data shared across modules for integration tests
-- Each module imports this and adds its own specific data

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
)
ON CONFLICT DO NOTHING;

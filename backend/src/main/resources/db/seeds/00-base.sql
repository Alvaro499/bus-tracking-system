-- ============================================================================
-- BASE SEEDS: Empresa y Bus
-- ============================================================================
-- Datos base que todo HU necesitará: empresa y vehículo principal
-- Dependencias: ninguna (son datos base)
-- ============================================================================

-- Insert base company
INSERT INTO companies.company (id, tax_id, name, email, phone, status, created_at, updated_at)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '3101000000', 'Empresa Test', 'test@test.com', '22000000', 'ACTIVE', NOW(), NOW());

-- Insert base bus for testing
INSERT INTO companies.bus (id, company_id, plate, internal_number, has_ramp, status, created_at, updated_at)
VALUES ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'CRC001', '001', false, 'ACTIVE', NOW(), NOW());

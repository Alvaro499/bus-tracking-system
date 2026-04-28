
---------- For UserStory #17: As a passenger, I want to see the real-time location of the bus on a map, so that I can track its arrival.

DELETE FROM tracking.bus_location WHERE bus_id = '650e8400-e29b-41d4-a716-446655440001';
DELETE FROM companies.bus WHERE id = '650e8400-e29b-41d4-a716-446655440001';
DELETE FROM companies.company WHERE id = '550e8400-e29b-41d4-a716-446655440000';

-- Insert test data
INSERT INTO companies.company (id, tax_id, name, email, phone, status, created_at, updated_at)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '3101000000', 'Empresa Test', 'test@test.com', '22000000', 'ACTIVE', NOW(), NOW());

INSERT INTO companies.bus (id, company_id, plate, internal_number, has_ramp, status, created_at, updated_at)
VALUES ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 'CRC001', '001', false, 'ACTIVE', NOW(), NOW());

INSERT INTO tracking.bus_location (bus_id, lat, lng, updated_at)
VALUES ('650e8400-e29b-41d4-a716-446655440001', 9.934739, -84.087502, NOW());

---------- UserStory #17 ----------
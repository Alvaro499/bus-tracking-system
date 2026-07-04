INSERT INTO tracking.bus_credential (id, bus_id, password_hash, issued_at, revoked_at)
VALUES (
  'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,
  '650e8400-e29b-41d4-a716-446655440001'::uuid,
  -- La contraseña en texto plano es "driver123"
  '$2a$12$.7aQ02RDJcqfbRGJrxKpX.LOaFKkeK6dYKDmChqijnL35N863Nn9a',
  NOW(),
  NULL
);
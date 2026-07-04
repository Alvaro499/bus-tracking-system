# ============================================================================
# PRUEBAS DE MÉTODOS HTTP DESDE LA TERMINAL (APIs REST)
# ============================================================================

# ----------------------------------------------------------------------------
# 🔐 POST - Login (Obtener Tokens / Enviar datos sensibles)
# ----------------------------------------------------------------------------
# 🖥️ BASH / WARP:
curl -v -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"busId": "650e8400-e29b-41d4-a716-446655440001", "password": "driver123"}'

# 💙 POWERSHELL:
$body = @{ busId = "650e8400-e29b-41d4-a716-446655440001"; password = "driver123" } | ConvertTo-Json
Invoke-WebRequest -Verbose -Method Post -Uri "http://localhost:8081/auth/login" -ContentType "application/json" -Body $body


# ----------------------------------------------------------------------------
# 🔍 GET - Listar o Buscar Recursos (Enviar Cookie de autenticación)
# ----------------------------------------------------------------------------
# 🖥️ BASH / WARP:
curl -v -X GET http://localhost:8081/tracking/trips/today \
  -H "Cookie: access_token=TU_TOKEN_AQUÍ"

# 💙 POWERSHELL:
Invoke-WebRequest -Verbose -Method Get -Uri "http://localhost:8081/tracking/trips/today" -Headers @{ "Cookie" = "access_token=TU_TOKEN_AQUÍ" }


# ----------------------------------------------------------------------------
# ➕ POST - Crear un Recurso (Ejemplo: Registrar una nueva alerta/incidente)
# ----------------------------------------------------------------------------
# 🖥️ BASH / WARP:
curl -v -X POST http://localhost:8081/tracking/incidents \
  -H "Content-Type: application/json" \
  -H "Cookie: access_token=TU_TOKEN_AQUÍ" \
  -d '{"busId": "650e8400-e29b-41d4-a716-446655440001", "type": "TRAFFIC", "description": "Congestionamiento vial en Av. Central"}'

# 💙 POWERSHELL:
$incidente = @{ busId = "650e8400-e29b-41d4-a716-446655440001"; type = "TRAFFIC"; description = "Congestionamiento" } | ConvertTo-Json
Invoke-WebRequest -Verbose -Method Post -Uri "http://localhost:8081/tracking/incidents" -ContentType "application/json" -Headers @{ "Cookie" = "access_token=TU_TOKEN_AQUÍ" } -Body $incidente


# ----------------------------------------------------------------------------
# 🔄 PUT - Actualizar un Recurso Completo (Ejemplo: Modificar datos de un bus)
# ----------------------------------------------------------------------------
# 🖥️ BASH / WARP:
curl -v -X PUT http://localhost:8081/companies/buses/650e8400-e29b-41d4-a716-446655440001 \
  -H "Content-Type: application/json" \
  -H "Cookie: access_token=TU_TOKEN_AQUÍ" \
  -d '{"plate": "M-12345", "capacity": 45, "model": "Mercedes-Benz 2026"}'

# 💙 POWERSHELL:
$busUpdate = @{ plate = "M-12345"; capacity = 45; model = "Mercedes-Benz 2026" } | ConvertTo-Json
Invoke-WebRequest -Verbose -Method Put -Uri "http://localhost:8081/companies/buses/650e8400-e29b-41d4-a716-446655440001" -ContentType "application/json" -Headers @{ "Cookie" = "access_token=TU_TOKEN_AQUÍ" } -Body $busUpdate


# ----------------------------------------------------------------------------
# 📝 PATCH - Actualizar Parcialmente (Ejemplo: Actualizar solo la localización)
# ----------------------------------------------------------------------------
# 🖥️ BASH / WARP:
curl -v -X PATCH http://localhost:8081/tracking/buses/650e8400-e29b-41d4-a716-446655440001/location \
  -H "Content-Type: application/json" \
  -H "Cookie: access_token=TU_TOKEN_AQUÍ" \
  -d '{"latitude": 9.9281, "longitude": -84.0907}'

# 💙 POWERSHELL:
$location = @{ latitude = 9.9281; longitude = -84.0907 } | ConvertTo-Json
Invoke-WebRequest -Verbose -Method Patch -Uri "http://localhost:8081/tracking/buses/650e8400-e29b-41d4-a716-446655440001/location" -ContentType "application/json" -Headers @{ "Cookie" = "access_token=TU_TOKEN_AQUÍ" } -Body $location


# ----------------------------------------------------------------------------
# 🗑️ DELETE - Eliminar un Recurso (Ejemplo: Cancelar una ruta o viaje)
# ----------------------------------------------------------------------------
# 🖥️ BASH / WARP:
curl -v -X DELETE http://localhost:8081/tracking/trips/987e6543-e21b-41d4-b716-112233445566 \
  -H "Cookie: access_token=TU_TOKEN_AQUÍ"

# 💙 POWERSHELL:
Invoke-WebRequest -Verbose -Method Delete -Uri "http://localhost:8081/tracking/trips/987e6543-e21b-41d4-b716-112233445566" -Headers @{ "Cookie" = "access_token=TU_TOKEN_AQUÍ" }
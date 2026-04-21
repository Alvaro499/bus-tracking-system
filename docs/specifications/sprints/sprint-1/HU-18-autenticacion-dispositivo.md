# HU-18 — Autenticar dispositivo del bus

**Usuario:** Técnico/Administrador  
**Valor:** Evitar que dispositivos no autorizados envíen ubicaciones falsas  
**Módulos:** tracking (backend), bus-app (frontend)  
**Bloqueante para:** HU-17

---

## Tasks

### Backend (tracking)
- [ ] **TASK-1** POST `/auth/bus/authenticate` - Generar JWT
  - Input: `{ placa, password }`
  - Validar credenciales en `bus_credential`
  - Response: `{ token: JWT, expiresIn: N_minutos }`

- [ ] **TASK-2** POST `/auth/bus/refresh` - Renovar JWT
  - Input: JWT expirado pero válido
  - Response: `{ token: nuevo_JWT }`

- [ ] **TASK-3** GenerateCredentialsService - Generar contraseña por bus
  - Generar password único + hash
  - Guardar en `bus_credential`
  - Devolver contraseña en texto plano (mostrar UNA VEZ)

- [ ] **TASK-4** Validar JWT en endpoints de ubicación (HU-17)
  - Middleware que valida Bearer token
  - Rechazar si token inválido o expirado

### Base de datos
- [x] Tabla `bus_credential` existe
- [ ] Agregar trigger o cron para limpiar tokens revocados

### Frontend (bus-app)
- [ ] **TASK-5** Pantalla de configuración inicial
  - Input: placa + contraseña (solo 1ª vez)
  - Llamar POST `/auth/bus/authenticate`
  - Guardar JWT en almacenamiento seguro
  - Ocultar campos tras éxito

- [ ] **TASK-6** Renovación automática de JWT
  - Detectar expiración próxima
  - Llamar POST `/auth/bus/refresh` antes que expire
  - Actualizar JWT en memoria

### Administrador (panel backend)
- [ ] **TASK-7** Endpoint POST `/companies/{busId}/regenerate-credential`
  - Generar nueva contraseña
  - Invalidar anterior y JWT activos
  - Devolver nueva contraseña

---

## Criterios de Aceptación

- **CA-01** ✅ Generar credenciales únicas por bus
- **CA-02** ✅ JWT obtenido con placa + password válidas
- **CA-03** ✅ Rechazar credenciales inválidas
- **CA-04** ✅ JWT se renueva automáticamente
- **CA-05** ✅ Regenerar contraseña invalida la anterior
- **CA-06** ✅ Chofer distinto usa mismo bus sin reconfigurar

---

## Definition of Done

- [x] BD: Tabla `bus_credential` existe
- [ ] Backend: Endpoints de auth implementados
- [ ] JWT: Provider creado y testeado
- [ ] Frontend: Configuración inicial + renovación automática
- [ ] Tests: Cobertura ≥ 80%
- [ ] Seguridad: Password hasheado (bcrypt o similar)

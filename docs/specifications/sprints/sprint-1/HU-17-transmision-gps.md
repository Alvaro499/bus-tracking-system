# HU-17 — Transmitir ubicación del bus en tiempo real

**Usuario:** Chofer  
**Valor:** Ver ubicación del bus en mapa público en tiempo real  
**Módulos:** tracking (backend), bus-app (frontend), company (backend:consumo de recursos)

---

## Tasks

### Backend (tracking)
- [ ] **TASK-1** POST `/tracking/buses/{busId}/location` - Recibir y guardar ubicación
  - Input: `{ lat, lng }`
  - Validar JWT del dispositivo (HU-18)
  - Guardar en `bus_location`
  - Response: `{ busId, lat, lng, updatedAt }`

- [ ] **TASK-2** GET `/tracking/buses/{busId}/location` - Devolver última ubicación
  - Input: `busId`
  - Validar que el viaje está `IN_PROGRESS`
  - Response: `{ busId, lat, lng, updatedAt, lastUpdatedSecondsAgo }`

### Frontend (bus-app)
- [ ] **TASK-3** Polling automático cada 5 segundos
  - Ejecutar solo si viaje está `IN_PROGRESS`
  - Parar si viaje finaliza o se cancela
  - Reintentar automáticamente si hay reconexión

### Mapa público (frontend)
- [ ] **TASK-4** Actualizar marcador del bus periódicamente
  - Consumir GET `/tracking/buses/{busId}/location`
  - Mostrar marcador solo si último update < N minutos
  
- [ ] **TASK-5** Indicador "última posición conocida"
  - Si sin datos > N minutos: mostrar "Última ubicación hace X min"

---

## Criterios de Aceptación

- **CA-01** ✅ Dispositivo transmite automáticamente al iniciar viaje
- **CA-02** ✅ Mapa se actualiza periódicamente
- **CA-03** ✅ Transmisión se detiene al finalizar viaje
- **CA-04** ✅ Reconexión automática
- **CA-05** ✅ Indicador de última posición conocida

---

## Definition of Done

- [x] BD: Tabla `bus_location` existe
- [ ] Backend: Endpoints implementados y testeados
- [ ] Frontend: Polling y visualización funcional
- [ ] Tests: Cobertura ≥ 80%
- [ ] Integración: HU-18 debe estar completa primero

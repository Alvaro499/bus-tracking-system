**HU-21 — Gestionar rutas a un bus**

> Como administrador de empresa, quiero asignar y desasignar rutas a cada bus de mi flota para que el chofer solo vea los viajes que su unidad puede realizar operativamente.

---

### **Descripción:**

El administrador accede al detalle de un bus desde el panel de flota y puede asignar una o varias rutas de su empresa a ese bus. A partir de ese momento, cuando el chofer tome ese bus, solo verá los viajes `PLANNED` del día que correspondan a las rutas asignadas. El administrador también puede desasignar rutas en cualquier momento.

---

### **Supuestos:**

- Solo el dueño y administradores de la empresa pueden asignar rutas a sus buses.
- Un bus puede tener asignadas múltiples rutas.
- Una ruta puede estar asignada a múltiples buses.
- Solo se pueden asignar rutas activas (`is_active = TRUE`) a un bus.
- Si un bus no tiene rutas asignadas, no aparecerán viajes disponibles en la app del chofer.
- No se puede desasignar una ruta de un bus si ese bus tiene un viaje `IN_PROGRESS` en esa ruta.
- Las rutas disponibles para asignar son únicamente las de la misma empresa.

---

### **Criterios de aceptación:**

- **CA-01** — Al ingresar al detalle de un bus, el administrador ve las rutas actualmente asignadas a ese bus.
- **CA-02** — Al asignar una ruta activa al bus, queda disponible de inmediato para los viajes del chofer.
- **CA-03** — Al intentar asignar una ruta ya asignada a ese bus, el sistema lo impide e informa el motivo.
- **CA-04** — Al intentar asignar una ruta inactiva, el sistema lo impide e informa el motivo.
- **CA-05** — Al desasignar una ruta, el bus deja de ver viajes de esa ruta en la app del chofer.
- **CA-06** — Al intentar desasignar una ruta con un viaje `IN_PROGRESS` en ese bus, el sistema lo impide e informa el motivo.
- **CA-07** — Las rutas disponibles para asignar pertenecen únicamente a la empresa del administrador.

---

### **Definition of Done:**

- [ ] Tabla `bus_route` creada con su relación `bus_id` + `route_id`.
- [ ] Endpoints de asignación y desasignación implementados y funcionales.
- [ ] Validación de ruta activa implementada.
- [ ] Bloqueo de desasignación para buses con viaje en curso implementado.
- [ ] Filtro de viajes por rutas asignadas aplicado en `GET /tracking/trips/today`.
- [ ] Los criterios CA-01 a CA-07 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

---

### **Mockup:**

```
─────────────────────────────────────────────
  ← Flota — Bus #14  SJB-1234
─────────────────────────────────────────────
  Rutas asignadas
  [ + Asignar ruta ]
─────────────────────────────────────────────
  Nombre              Origen        Destino
  ──────────────────────────────────────────
  Ruta 300-C          Terminal SJ   Cartago    [Desasignar]
  Ruta 400-A          Alajuela      San José   [Desasignar]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Asignar ruta — Bus #14
─────────────────────────────────────────────
  Ruta *
  [ Ruta 300-C  ▼ ]

─────────────────────────────────────────────
  [ Guardar ]
─────────────────────────────────────────────
```

---
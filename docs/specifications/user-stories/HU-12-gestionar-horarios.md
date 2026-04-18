
---

**HU-12 — Gestionar horarios de una ruta**

> Como administrador de empresa, quiero registrar, editar y desactivar horarios de las rutas de mi empresa para que los usuarios vean correctamente a qué horas opera cada ruta.

**Descripción:**

El administrador gestiona los horarios desde el detalle de una ruta. Puede agregar horarios indicando hora de salida, días de la semana y fechas de vigencia. También puede editarlos, desactivarlos y consultarlos. Una ruta puede tener múltiples horarios y cada horario puede aplicar a uno o varios días de la semana.

**Supuestos:**

- Solo el dueño y administradores de la empresa pueden gestionar sus horarios.

- Un horario (`schedule`) define el patrón recurrente: hora de salida, días de la semana y rango de fechas de vigencia.

- Se puede editar un horario en cualquier momento. Los viajes con estado `IN_PROGRESS` no se ven afectados en su ejecución real, ya que sus tiempos reales se registran en `actual_start_time` y `actual_end_time`. Los viajes con estado `PLANNED` reflejan el cambio automáticamente al leer directamente del schedule actualizado, sin necesidad de regeneración.

- Desactivar un horario (`is_active = FALSE`) impide generar nuevos viajes pero no cancela los ya existentes.

- Un horario desactivado no aparece en la consulta pública de viajes del día.

- No se eliminan horarios físicamente para preservar el historial de viajes.


**Criterios de aceptación:**

- **CA-01** — Al registrar un horario con hora de salida, días de la semana y fecha de inicio válidos, el horario queda activo para esa ruta.
- **CA-02** — Al intentar registrar un horario duplicado para la misma ruta, día y hora de salida, el sistema lo impide e informa el motivo.
- **CA-03** — Al dejar campos obligatorios vacíos, el proceso no se completa y se indica el campo faltante.
- **CA-04** — Al editar un horario, los viajes con estado `PLANNED` asociados se regeneran automáticamente con la información actualizada.
- **CA-05** — Al desactivar un horario, deja de aparecer en la consulta pública pero se conserva en el historial.
- **CA-06** — Al consultar los horarios de una ruta, el administrador los ve organizados por día de la semana.
- **CA-07** — El administrador puede seleccionar múltiples días de la semana al crear un horario para no tener que crearlo día por día.

**Definition of Done:**

- [ ] CRUD de horarios implementado y funcional para dueño y administrador de empresa.
- [ ] Los criterios CA-01 a CA-07 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```
─────────────────────────────────────────────
  ← Rutas — Ruta 300-C
  Terminal SJ → Cartago
  [ Ver paradas ]   [ Ver horarios ]
─────────────────────────────────────────────

─────────────────────────────────────────────
  Horarios — Ruta 300-C
─────────────────────────────────────────────
  [ + Agregar horario ]
─────────────────────────────────────────────
  Lunes
  ──────────────────────────────────────────
  05:15   Vigente: 01/01/2025 – indefinido   ACTIVE   [Editar]
  06:00   Vigente: 01/01/2025 – indefinido   ACTIVE   [Editar]

  Martes
  ──────────────────────────────────────────
  05:15   Vigente: 01/01/2025 – indefinido   ACTIVE   [Editar]

  Miércoles
  ──────────────────────────────────────────
  (sin horarios)
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Agregar / Editar horario
─────────────────────────────────────────────
  Hora de salida *
  [ 05:15                              ]

  Días de la semana *
  [x] Lun  [x] Mar  [ ] Mié  [ ] Jue
  [x] Vie  [ ] Sáb  [ ] Dom

  Fecha de inicio *
  [ 01/01/2025                         ]

  Fecha de fin
  [ indefinido                         ]

  Estado *
  [ ACTIVE              ▼ ]
─────────────────────────────────────────────
  [ Guardar ]
─────────────────────────────────────────────
```

---
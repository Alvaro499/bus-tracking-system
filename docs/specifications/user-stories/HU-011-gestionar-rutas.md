
---

**HU-11 — Gestionar rutas**

> Como administrador de empresa, quiero registrar, consultar, editar e inhabilitar las rutas de mi empresa para que los usuarios vean correctamente los recorridos disponibles.

**Descripción:**

El administrador puede gestionar las rutas de su empresa desde un panel interno. Puede registrar nuevas rutas con su origen y destino, editarlas, inhabilitarlas y consultarlas. Las rutas inactivas no aparecen en las búsquedas públicas pero se conservan en el sistema por historial.

**Supuestos:**

- Solo el dueño y administradores de la empresa pueden gestionar sus rutas. Las rutas de distintas empresas están aisladas entre sí.
- Una ruta requiere al menos un campo `is_active` para poder inhabilitarse sin eliminarse físicamente. Esto implica agregar dicho campo a la tabla `route`.
- Se puede editar la información descriptiva de una ruta (nombre, origen, destino, tarifa plana) en cualquier momento.
- No se pueden modificar las paradas de una ruta si tiene un viaje con estado `IN_PROGRESS`.
- No se eliminan rutas físicamente para preservar el historial de viajes asociados.
- Una ruta inactiva no genera nuevos viajes pero conserva su historial.
- Las rutas se identifican internamente por UUID.

**Criterios de aceptación:**

- **CA-01** — Al registrar una ruta con campos obligatorios válidos, la ruta queda disponible para agregarle paradas y horarios.
- **CA-02** — Al intentar registrar una ruta con campos obligatorios vacíos, el proceso no se completa y se indica el campo faltante.
- **CA-03** — Al editar la información descriptiva de una ruta, los cambios se reflejan de inmediato en el sistema.
- **CA-04** — Al intentar modificar las paradas de una ruta con un viaje en curso, el sistema lo impide e informa el motivo.
- **CA-05** — Al inhabilitar una ruta, deja de aparecer en las búsquedas públicas pero se conserva en el historial.
- **CA-06** — Al consultar las rutas, el administrador ve todas las rutas de su empresa con su estado actual.
- **CA-07** — Las rutas de otras empresas no son visibles ni modificables desde el panel.

**Definition of Done:**

- [ ] CRUD de rutas implementado y funcional para dueño y administrador de empresa.
- [ ] Campo `is_active` agregado a la tabla `route` en la BD.
- [ ] Bloqueo de edición de paradas implementado para rutas con viaje en curso.
- [ ] Los criterios CA-01 a CA-07 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```
─────────────────────────────────────────────
  ← Mi empresa — Rutas
─────────────────────────────────────────────
  [ + Agregar ruta ]
─────────────────────────────────────────────
  Nombre              Origen      Destino    Estado
  ────────────────────────────────────────────────
  Ruta 300-C          Terminal SJ  Cartago   ACTIVE   [Editar]
  Ruta 400-A          Alajuela     SJ        INACTIVE [Editar]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Agregar / Editar ruta
─────────────────────────────────────────────
  Nombre *
  [ Ruta 300-C                         ]

  Origen *
  [ Terminal San José                  ]

  Destino *
  [ Cartago                            ]

  Tarifa plana
  ( ) Sí   (•) No

  Estado *
  [ ACTIVE              ▼ ]
─────────────────────────────────────────────
  [ Guardar ]
─────────────────────────────────────────────
```

---

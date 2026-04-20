
---

**HU-14 — Gestionar paradas de una ruta**

> Como administrador de empresa, quiero asignar, reordenar y quitar paradas de una ruta para que el recorrido mostrado a los usuarios sea correcto.

**Descripción:**

Desde el detalle de una ruta, el administrador puede asignar paradas del catálogo de la empresa a esa ruta, definir su orden de recorrido y el tiempo estimado entre cada una. También puede quitar paradas de la ruta o reordenarlas.

**Supuestos:**

- Las paradas disponibles para asignar provienen del catálogo de la empresa (ver HU-13).
- Las paradas llevan un orden establecido por el administrador.
- No se pueden modificar las paradas de una ruta si tiene un viaje con estado `IN_PROGRESS`.
- Quitar una parada de una ruta no la elimina del catálogo general.

**Criterios de aceptación:**

- **CA-01** — Al asignar una parada del catálogo a la ruta, aparece en el recorrido con un orden definido.
- **CA-02** — Al reordenar las paradas, el nuevo orden se refleja en el mapa y la lista pública.
- **CA-03** — Al quitar una parada de la ruta, desaparece del recorrido pero se conserva en el catálogo.
- **CA-04** — Al intentar modificar las paradas de una ruta con un viaje en curso, el sistema lo impide e informa el motivo.
- **CA-05** — Al asignar una parada, el administrador puede indicar opcionalmente el tiempo estimado desde el origen.
- **CA-06** — Al consultar las paradas de una ruta, se muestran en orden de recorrido de origen a destino.

**Definition of Done:**

- [ ] Asignación, reordenamiento y eliminación de paradas por ruta implementados y funcionales.
- [ ] Bloqueo de modificación para rutas con viaje en curso implementado.
- [ ] Los criterios CA-01 a CA-06 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```
─────────────────────────────────────────────
  ← Rutas — Ruta 300-C
  Terminal SJ → Cartago
  [ Ver paradas ]   [ Ver horarios ]
─────────────────────────────────────────────
  Paradas — Ruta 300-C
─────────────────────────────────────────────
  [ + Asignar parada ]
─────────────────────────────────────────────
  #   Nombre              T. estimado
  ──────────────────────────────────────────
  1   Terminal San José   0 min       [↑][↓] [Quitar]
  2   La Sabana           12 min      [↑][↓] [Quitar]
  3   Paseo Colón         20 min      [↑][↓] [Quitar]
  4   Tres Ríos           45 min      [↑][↓] [Quitar]
  5   Cartago             60 min      [↑][↓] [Quitar]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Asignar parada — Ruta 300-C
─────────────────────────────────────────────
  Parada *
  [ Terminal San José   ▼ ]

  Tiempo estimado desde origen (min)
  [ 20                                 ]
─────────────────────────────────────────────
  [ Guardar ]
─────────────────────────────────────────────
```

---

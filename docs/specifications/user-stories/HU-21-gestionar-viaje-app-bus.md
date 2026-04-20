
---

**HU-21 — Gestionar estado del viaje desde la app del bus**

> Como chofer, quiero gestionar el estado de mi viaje desde la app del bus para que los usuarios vean información actualizada en tiempo real.

**Descripción:**

El chofer accede a la app del bus y ve los viajes asignados para el día actual. Selecciona el viaje que realizará, lo inicia, confirma las paradas por las que va pasando y lo finaliza o cancela según corresponda. Cada acción actualiza el estado del viaje en el sistema y afecta lo que los usuarios ven en el mapa público.

**Supuestos:**

- Los viajes del día se generan automáticamente cada día a medianoche como `PLANNED` a partir de los schedules activos.
- Un viaje solo puede ser tomado por un bus a la vez.
- Un viaje cancelado no puede volver a iniciarse.
- El estado `REASSIGNED` se elimina. Si un bus no puede realizar un viaje, el administrador reasigna otro bus desde el panel.
- Las paradas confirmadas no pueden deshacerse.
- Al iniciar el viaje, la transmisión de ubicación comienza automáticamente (ver HU-17).
- Al finalizar o cancelar el viaje, la transmisión de ubicación se detiene automáticamente.
- La cancelación requiere un motivo obligatorio.
- Los relevos solicitados requieren un motivo obligatorio.

**Criterios de aceptación:**

- **CA-01** — Al abrir la app, el chofer ve los viajes del día con estado `PLANNED` disponibles para tomar.
- **CA-02** — Al seleccionar un viaje y confirmarlo, el estado cambia a `IN_PROGRESS` y comienza la transmisión de ubicación.
- **CA-03** — Al intentar tomar un viaje ya tomado por otro bus, el sistema lo impide e informa el motivo.
- **CA-04** — Durante el viaje, el chofer puede marcar cada parada como completada en orden.
- **CA-05** — Las paradas ya confirmadas no pueden deshacerse.
- **CA-06** — Al finalizar el viaje, el estado cambia a `COMPLETED` y la transmisión de ubicación se detiene.
- **CA-07** — Al cancelar el viaje, el estado cambia a `CANCELLED`, se registra el motivo y la transmisión se detiene.
- **CA-08** — Un viaje cancelado no puede volver a iniciarse.

- ***CA-09** - Los viajes del día se generan automáticamente cada día a medianoche como PLANNED a partir de los horarios activos. Si un viaje del día anterior aún no ha terminado (IN_PROGRESS), se conserva en su estado actual sin modificarse.

**Definition of Done:**

- [ ] Flujo completo de gestión de viaje implementado en la app del bus.
- [ ] Generación automática diaria de trips desde schedules activos implementada.
- [ ] Integración con transmisión de ubicación (HU-17) funcionando correctamente.
- [ ] Los criterios CA-01 a CA-08 están cubiertos por pruebas automatizadas.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```
─────────────────────────────────────────────
  Viajes disponibles — Hoy
─────────────────────────────────────────────
  Ruta 300-C   Terminal SJ → Cartago
  Salida: 05:45 a.m.                  [Tomar]

  Ruta 300-C   Terminal SJ → Cartago
  Salida: 06:30 a.m.                  [Tomar]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Viaje en curso — Ruta 300-C
  Terminal SJ → Cartago — 05:45 a.m.
  Transmitiendo ubicación... ●
─────────────────────────────────────────────
  Paradas
  ──────────────────────────────────────────
  ✓  Terminal San José
  ✓  La Sabana
  →  Paseo Colón                [Confirmar]
     Tres Ríos
     Cartago
─────────────────────────────────────────────
  [ Finalizar viaje ]   [ Cancelar viaje ]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Cancelar viaje
─────────────────────────────────────────────
  Motivo *
  [ Falla mecánica                     ]

─────────────────────────────────────────────
  [ Confirmar cancelación ]
─────────────────────────────────────────────
```

---


---

**HU-17 — Gestionar y transmitir el viaje en tiempo real desde la app del bus**

> Como chofer, quiero gestionar el estado de mi viaje desde la app del bus y que mi ubicación se transmita automáticamente mientras conduzco, para que los pasajeros puedan ver dónde está el bus en tiempo real.

**Dependencias:**
- SPIKE-01 ✅ completado
- HU-18 — autenticación del dispositivo con JWT

**Descripción:**

El chofer accede a la app del bus y ve los viajes asignados para el día actual. Selecciona el viaje que realizará y lo inicia — a partir de ese momento el dispositivo transmite su ubicación automáticamente sin intervención adicional. Durante el viaje puede confirmar las paradas por las que va pasando. Al finalizar o cancelar el viaje, la transmisión se detiene automáticamente y el bus desaparece del mapa público. Si el dispositivo pierde señal, retoma la transmisión automáticamente al recuperarla.

**Supuestos:**

- Esta funcionalidad vive en `management-app`, en una sección exclusiva para el chofer.
- El dispositivo debe estar autenticado antes de poder transmitir (ver HU-18).
- Los viajes del día se generan automáticamente cada día a medianoche como `PLANNED` a partir de los horarios activos.
- La transmisión ocurre únicamente mientras el viaje tiene estado `IN_PROGRESS`.
- Al iniciar el viaje, la transmisión comienza automáticamente — el chofer no activa nada extra.
- Al finalizar o cancelar el viaje, la transmisión se detiene automáticamente.
- El sistema almacena solo la última posición conocida del bus, no el historial de coordenadas.
- La frecuencia de transmisión es cada 5 segundos — validado en SPIKE-01.
- Un viaje cancelado no puede volver a iniciarse.
- La cancelación requiere un motivo obligatorio.
- Un viaje solo puede ser tomado por un bus a la vez.
- Si un viaje del día anterior aún está `IN_PROGRESS`, se conserva en su estado actual sin modificarse.
- GPS requiere HTTPS en dispositivos móviles — confirmado en SPIKE-01.

**Criterios de aceptación:**

- **CA-01** — Al abrir la app, el chofer ve los viajes del día con estado `PLANNED` disponibles para tomar.
- **CA-02** — Al seleccionar un viaje y confirmarlo, el estado cambia a `IN_PROGRESS` y la transmisión de ubicación comienza automáticamente.
- **CA-03** — Al intentar tomar un viaje ya tomado por otro bus, el sistema lo impide e informa el motivo.
- **CA-04** — Durante el viaje, el chofer puede marcar cada parada como completada en orden.
- **CA-05** — Las paradas ya confirmadas no pueden deshacerse.
- **CA-06** — La posición del bus en el mapa público se actualiza cada 5 segundos mientras el viaje está `IN_PROGRESS`.
- **CA-07** — Si el dispositivo pierde señal y la recupera, retoma la transmisión automáticamente sin intervención del chofer.
- **CA-08** — Al finalizar el viaje, el estado cambia a `COMPLETED` y la transmisión se detiene automáticamente.
- **CA-09** — Al cancelar el viaje, el estado cambia a `CANCELLED`, se registra el motivo y la transmisión se detiene automáticamente.
- **CA-10** — Un viaje cancelado no puede volver a iniciarse.

**Definition of Done:**

- [ ] Lista de viajes del día implementada y funcional en la app del bus
- [ ] Flujo completo de inicio, confirmación de paradas, finalización y cancelación implementado
- [ ] Transmisión de ubicación vinculada al estado `IN_PROGRESS` del viaje
- [ ] Transmisión se detiene automáticamente al cambiar el estado a `COMPLETED` o `CANCELLED`
- [ ] JWT incluido en cada petición de transmisión
- [ ] Reconexión automática de transmisión implementada
- [ ] Generación automática diaria de viajes desde horarios activos implementada
- [ ] Probado en dispositivo móvil real con HTTPS
- [ ] Los criterios CA-01 a CA-10 están cubiertos por pruebas automatizadas
- [ ] Interfaz funcional en móvil y escritorio
- [ ] Sin bugs críticos abiertos relacionados a esta HU

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

  Última transmisión: hace 3 seg
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

```
─────────────────────────────────────────────
  Sin señal
  Última posición conocida: hace 8 min
  Se retomará automáticamente al
  recuperar la conexión.
─────────────────────────────────────────────
```

---
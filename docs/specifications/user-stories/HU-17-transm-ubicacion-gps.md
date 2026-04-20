
---

**HU-17 — Transmitir ubicación del bus en tiempo real**

> Como chofer, quiero que el dispositivo transmita mi ubicación automáticamente mientras el viaje está activo para que los usuarios puedan ver dónde está el bus en tiempo real.

**Descripción:**

El chofer inicia su viaje asignado desde la app del bus. A partir de ese momento el dispositivo transmite la ubicación automáticamente de forma periódica sin intervención manual. Al finalizar el viaje, la transmisión se detiene y el bus desaparece del mapa público. Si se pierde la señal, el sistema retoma la transmisión automáticamente al recuperarla.

**Supuestos:**

- Esta funcionalidad vive en una app separada instalada en el dispositivo del bus, distinta a la app pública de usuarios.
- El dispositivo debe estar autenticado antes de poder transmitir (ver HU-18).
- La transmisión ocurre únicamente mientras el viaje tiene estado `IN_PROGRESS`.
- El sistema almacena solo la última posición conocida del bus, no el historial de coordenadas.
- La frecuencia de transmisión se define con el PO (valor inicial sugerido: cada 5 segundos).
- Si el dispositivo pierde señal por más de N minutos, el marcador del bus cambia visualmente en el mapa público indicando la última posición conocida.

**Criterios de aceptación:**

- **CA-01** — Al iniciar el viaje desde la app, el dispositivo comienza a transmitir la ubicación automáticamente sin intervención adicional del chofer.
- **CA-02** — La posición del bus en el mapa público se actualiza periódicamente mientras el viaje está activo.
- **CA-03** — Al finalizar el viaje desde la app, la transmisión se detiene y el bus desaparece del mapa público.
- **CA-04** — Si el dispositivo pierde señal y la recupera, retoma la transmisión automáticamente sin intervención del chofer.
- **CA-05** — Si el dispositivo lleva más de N minutos sin transmitir, el mapa público indica la última posición conocida y el tiempo transcurrido.

**Definition of Done:**

- [ ] Transmisión automática de ubicación implementada y funcional en la app del bus.
- [ ] Inicio y fin de transmisión vinculados al estado del viaje.
- [ ] Lógica de reconexión automática implementada.
- [ ] Indicador de última posición conocida funcionando en el mapa público.
- [ ] Los criterios CA-01 a CA-05 están cubiertos por pruebas automatizadas.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup — app del bus:**

```
─────────────────────────────────────────────
  Viaje asignado
  Ruta 300-C — Terminal SJ → Cartago
  Salida: 05:45 a.m.
─────────────────────────────────────────────
  Estado: EN CURSO
  Transmitiendo ubicación...  ● 

  Última transmisión: hace 3 seg
─────────────────────────────────────────────
  [ Finalizar viaje ]
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

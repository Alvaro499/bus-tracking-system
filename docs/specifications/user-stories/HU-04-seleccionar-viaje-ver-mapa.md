---

**HU-04 — Seleccionar un viaje y ver el bus en el mapa**

> Como usuario público, quiero seleccionar un viaje de la lista y ver el bus correspondiente en el mapa, para saber exactamente dónde está el autobús que voy a tomar.

**Descripción:**

Desde la página de viajes del día (HU-03), el usuario toca un viaje y el sistema lo lleva al mapa con el bus ubicado en tiempo real. Si el viaje no está en curso, el sistema informa el motivo en lugar de mostrar el mapa. No se requiere iniciar sesión.

**Supuestos:**

- Esta página es la Pantalla 3 definida en el flujo de HU-01.
- Solo los viajes En curso llevan al mapa con un bus visible.
- Los viajes Preparados y Finalizados también son seleccionables, pero muestran un mensaje en lugar del mapa.
- El comportamiento del mapa y la actualización automática son los definidos en HU-01.

**Criterios de aceptación:**

- **CA-01** — Al seleccionar un viaje En curso, el usuario ve el mapa centrado en la posición actual del bus con empresa, número de unidad y ruta.
- **CA-02** — Al seleccionar un viaje Preparado, se muestra el mensaje: "Este servicio aún no ha iniciado."
- **CA-03** — Al seleccionar un viaje Finalizado, se muestra el mensaje: "Este servicio ya terminó."
- **CA-04** — Al seleccionar un viaje Suspendido, se muestra el mensaje: "Este servicio fue suspendido."
- **CA-05** — Si un bus pierde la señal, se muestra un mensaje indicando la última posisción y la ultimo tiempo registrado."


**Definition of Done:**

- [ ] Pantalla de mapa implementada y accesible desde HU-03.
- [ ] Los criterios CA-01 a CA-05 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.



**Mockup**

![Mapa](./assets/hu04-mapa-mockup.svg)



---

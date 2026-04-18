
---

**HU-01 — Ver ubicación de un bus en el mapa a partir de la búsqueda de una ruta**

> Como usuario público, quiero buscar una ruta y seleccionar un viaje para ver en el mapa la ubicación actual del bus asignado para saber dónde está el bus que quiero tomar.

**Descripción:**

El usuario ingresa a la aplicación y usa el buscador para encontrar una ruta (ej: "San José – Cartago"). El sistema muestra las empresas que la operan y los viajes programados por dicha empresa para el día actual, indicando si cada uno está pendiente, en curso o finalizado. Al seleccionar un viaje en curso, el mapa centra la vista en el bus y se actualiza automáticamente. No se requiere iniciar sesión.


**Supuestos:**

- Solo se muestran viajes del día actual.
- Los estados posibles de un viaje son: Preparado (aún no ha salido), En curso (bus activo en ruta), Finalizado (llegó a destino), y opcionalmente Suspendido (cancelado por causa externa — confirmar con el PO si aplica).
- Los viajes Finalizados se muestran en la lista pero apagados visualmente.
- Los viajes Preparados se muestran en la lista pero al seleccionarlos no se muestra ningún bus en el mapa.
- Solo los viajes En curso muestran un bus en el mapa.
- Cada viaje tiene un bus asignado previamente por el administrador de la empresa.
- No se requiere iniciar sesión para ver el mapa


**Criterios de aceptación:**

- **CA-01** — Cuando el usuario busca una ruta, el sistema muestra las rutas coincidentes con sus empresas y variantes disponibles.
- **CA-02** — Al seleccionar una ruta y empresa, el usuario ve la lista de viajes del día con su estado: _pendiente_, _en curso_ o _finalizado_.
- **CA-03** — Al seleccionar un viaje _en curso_, el mapa muestra el marcador del bus con empresa, número de unidad y ruta.
- **CA-04** — La posición del marcador se actualiza automáticamente cada N segundos sin recargar la página.
- **CA-05** — Al seleccionar un viaje _pendiente_, se muestra el mensaje: "Este servicio aún no ha iniciado."
- **CA-06** — Al seleccionar un viaje _finalizado_, se muestra el mensaje: "Este servicio ya terminó."
- **CA-07** — Si un bus lleva más de N minutos sin actualizar su posición, su marcador cambia visualmente e indica "Última ubicación conocida hace X minutos."
- **CA-08** — Si no hay viajes en curso para la ruta buscada, el mapa aparece vacío con el aviso: "No hay buses en movimiento para esta ruta en este momento."
- **CA-09** — El usuario puede buscar rutas y ver el mapa sin iniciar sesión.


**Definition of Done:**

- [ ]  Buscador, listado de viajes y mapa implementados y funcionando en conjunto.
- [ ]  Los criterios CA-01 a CA-09 están cubiertos por pruebas automatizadas.
- [ ]  Cobertura de pruebas unitarias ≥ 80% en la lógica de filtrado de viajes y actualización de marcadores.
- [ ]  Interfaz funcional en móvil y escritorio.
- [ ]  Sin bugs críticos abiertos relacionados a esta HU.


**Mockup**

![Búsqueda de rutas y visualización en mapa](./assets/hu01-mockup.svg)


---

---

**HU-03 — Ver viajes del día de una empresa**

> Como usuario público, quiero ver la lista de viajes programados para hoy de una empresa al seleccionarla, para saber a qué hora sale el bus que necesito.

**Descripción:**

Luego de seleccionar una de las rutas de cualquiera de las empresas desde los resultados de búsqueda, el usuario llega a una página que lista todos los viajes del día de esa empresa para la ruta buscada, cada uno con su hora de salida, origen, destino, hora de llegada y estado actual. No se requiere iniciar sesión.

**Supuestos:**

- Esta página es la Pantalla 2 definida en el flujo de HU-01.
- Los estados y reglas de visibilidad de los viajes son los mismos definidos en los supuestos de HU-01.
- La hora de llegada muestra `---` si el viaje está Preparado o Suspendido, y la hora real si está En curso o Finalizado.
- Al seleccionar un viaje Preparado o Finalizado no ocurre ninguna acción de navegación; la fila puede resaltarse visualmente pero no lleva a ninguna pantalla nueva.
- Si la empresa no tiene viajes registrados para hoy en esa ruta, se muestra un mensaje informativo.

**Criterios de aceptación:**

- **CA-01** — Al ingresar a la página, el usuario ve la lista de viajes del día con hora de salida, origen, destino, hora de llegada y estado de cada uno.
- **CA-02** — Cada viaje muestra visualmente su estado: Preparado, En curso, Finalizado o Suspendido.
- **CA-03** — Si no hay viajes registrados para la ruta y empresa seleccionadas en el día actual, se muestra el mensaje: "No hay viajes programados para esta ruta hoy."
- **CA-04** — El encabezado de la página muestra el nombre de la empresa con un enlace a su perfil (ver HU-02).
- **CA-05** — La lista se actualiza automáticamente cada N segundos para reflejar cambios de estado sin recargar la página.
- **CA-06** — Al seleccionar un viaje En curso, el sistema muestra la ubicación del bus en el mapa (ver HU-04).

**Definition of Done:**

- [ ] Página de viajes del día implementada y accesible desde el flujo de búsqueda de HU-01.
- [ ] Los criterios CA-01 a CA-05 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

---

¿Seguimos con HU-04?
**Mockcup**

```
─────────────────────────────────────────────
  ← Cartago – San José
  LUMACA  [ver perfil]
─────────────────────────────────────────────
  Salida    Origen        Destino     Llegada   Estado
  ─────────────────────────────────────────────────────
  05:15     Terminal SJ   Cartago     ---        Preparado
  05:30     Terminal SJ   Cartago     ---        Preparado
  05:45     Terminal SJ   Cartago     06:58      En curso  ●
  06:00     Terminal SJ   Cartago     06:47      Finalizado
  06:15     Terminal SJ   Cartago     ---        Suspendido
  06:30     Terminal SJ   Cartago     ---        Preparado
  ...
─────────────────────────────────────────────
  No hay más viajes programados para hoy.
─────────────────────────────────────────────

```

---
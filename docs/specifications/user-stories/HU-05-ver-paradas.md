
---

**HU-05 — Ver paradas de una ruta**

> Como usuario público, quiero ver las paradas de la ruta en el mapa y en una lista, para saber por dónde pasa el bus y dónde puedo abordarlo.

**Descripción:**

Dentro de la pantalla del mapa (HU-04), el usuario puede activar un toggle para mostrar las paradas de la ruta como marcadores sobre el mapa. Debajo del mapa se muestra siempre la lista ordenada de paradas con su nombre o referencia. Las paradas pertenecen a la ruta, por lo que son las mismas para todos los viajes de esa ruta. No se requiere iniciar sesión.

**Supuestos:**

- Las paradas son fijas por ruta, no varían entre viajes de la misma ruta.
- Las paradas se muestran en orden de recorrido, de origen a destino.
- Si la empresa actualiza una parada, el cambio se refleja inmediatamente para el usuario.
- La lista de paradas es de solo lectura para el usuario público.

**Criterios de aceptación:**

- **CA-01** — Al activar el toggle de paradas, los marcadores de paradas aparecen sobre el mapa en orden de recorrido.
- **CA-02** — Al tocar un marcador de parada en el mapa, se muestra el nombre o referencia de esa parada.
- **CA-03** — Debajo del mapa se muestra una lista ordenada de todas las paradas de la ruta con su nombre o referencia.
- **CA-04** — Si la empresa modifica una parada, el usuario ve la información actualizada al consultar.

**Definition of Done:**

- [ ] Toggle de paradas implementado y funcional sobre el mapa de HU-04.
- [ ] Lista de paradas visible debajo del mapa.
- [ ] Los criterios CA-01 a CA-04 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

![Mapa](./assets/hu04-mapa-mapas-mockup.svg)

---

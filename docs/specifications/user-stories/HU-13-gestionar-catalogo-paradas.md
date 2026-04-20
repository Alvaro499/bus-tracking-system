
---
**HU-13 — Gestionar Catálogo de paradas**

> Como administrador de empresa, quiero registrar, editar y consultar las paradas de mi empresa para tener un catálogo de paradas reutilizables en mis rutas.

**Descripción:**

El administrador gestiona las paradas desde un catálogo general de la empresa, independiente de las rutas. Puede registrar nuevas paradas con nombre, coordenadas y referencia, editarlas y consultarlas. Una parada registrada puede luego asignarse a una o varias rutas (ver HU-14).

**Supuestos:**

- Las paradas pertenecen a la empresa, no a una ruta específica. Una misma parada puede usarse en múltiples rutas de la misma empresa.
- Los campos editables son: nombre, latitud, longitud y referencia.
- No se eliminan paradas físicamente si están asignadas a alguna ruta, para preservar la integridad del recorrido.
- Si una parada no está asignada a ninguna ruta, sí puede eliminarse.
- Editar una parada afecta a todas las rutas que la usan, ya que estas la referencian directamente.
- Las paradas se identifican internamente por UUID.

**Criterios de aceptación:**

- **CA-01** — Al registrar una parada con nombre y coordenadas válidos, queda disponible en el catálogo de paradas de la empresa.
- **CA-02** — Al dejar campos obligatorios vacíos, el proceso no se completa y se indica el campo faltante.
- **CA-03** — Al editar una parada, los cambios se reflejan en todas las rutas que la utilizan.
- **CA-04** — Al intentar eliminar una parada asignada a una o más rutas, el sistema lo impide e informa el motivo.
- **CA-05** — Al eliminar una parada sin asignaciones, se elimina correctamente del catálogo.
- **CA-06** — Al consultar el catálogo, el administrador ve todas las paradas de su empresa.

**Definition of Done:**

- [ ] CRUD de paradas implementado y funcional para dueño y administrador de empresa.
- [ ] Bloqueo de eliminación para paradas asignadas a rutas implementado.
- [ ] Los criterios CA-01 a CA-06 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```
─────────────────────────────────────────────
  ← Mi empresa — Paradas
─────────────────────────────────────────────
  [ + Agregar parada ]
─────────────────────────────────────────────
  Nombre              Referencia         
  ──────────────────────────────────────────
  Terminal San José   Frente al ICE      [Editar]
  La Sabana           Parque La Sabana   [Editar]
  Tres Ríos           Entrada principal  [Editar]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Agregar / Editar parada
─────────────────────────────────────────────
  Nombre *
  [ Terminal San José                  ]

  Latitud *
  [ 9.933611                           ]

  Longitud *
  [ -84.079444                         ]

  Referencia
  [ Frente al ICE, 100m al norte       ]
─────────────────────────────────────────────
  [ Guardar ]
─────────────────────────────────────────────
```

---

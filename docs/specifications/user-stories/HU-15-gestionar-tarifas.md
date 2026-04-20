
---

**HU-15 — Gestionar Tarifas de Rutas**

> Como administrador de empresa, quiero registrar y actualizar los precios de una ruta para que los usuarios tengan esa información como referencia antes de viajar.

**Descripción:**

El administrador define los precios desde el detalle de una ruta. Una ruta puede manejar tarifa plana (un único precio para todo el recorrido) o tarifa variable (un precio distinto por cada parada). Los precios son informativos, el sistema no gestiona pagos. Se puede programar la vigencia de un precio para que entre en efecto en una fecha futura.

**Supuestos:**

- El tipo de tarifa (plana o variable) se define al crear la ruta y condiciona cómo se gestionan los precios.
- Solo el dueño y administradores de la empresa pueden gestionar los precios de sus rutas.
- Los precios tienen fechas de vigencia, lo que permite programar cambios anticipadamente.
- Un precio vencido deja de mostrarse al usuario público automáticamente.

**Criterios de aceptación:**

- **CA-01** — Si la ruta es de tarifa plana, al registrar un precio único el usuario público lo ve al consultar la ruta.
- **CA-02** — Si la ruta es de tarifa variable, el administrador puede definir un precio por cada parada.
- **CA-03** — Al actualizar un precio, el usuario público ve la información actualizada.
- **CA-04** — Al consultar una ruta de tarifa variable, el usuario público ve el precio asociado a cada parada.
- **CA-05** — Al definir un precio con fecha de inicio futura, el sistema lo aplica automáticamente en esa fecha.

**Definition of Done:**

- [ ] Gestión de precio plano y precio por parada implementados y funcionales.
- [ ] Vigencia de precios funcionando correctamente.
- [ ] Los criterios CA-01 a CA-05 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup — tarifa plana:**

```
─────────────────────────────────────────────
  ← Ruta 300-C — Precios
  Tarifa: Plana
─────────────────────────────────────────────
  Precio por pasaje *
  [ ₡800.00                            ]

  Vigencia
  Desde [ 01/01/2025 ]  Hasta [ indefinido ]
─────────────────────────────────────────────
  [ Guardar ]
─────────────────────────────────────────────
```

**Mockup — tarifa variable:**

```
─────────────────────────────────────────────
  ← Ruta 300-C — Precios
  Tarifa: Variable por parada
─────────────────────────────────────────────
  Parada              Precio      Vigencia
  ──────────────────────────────────────────
  Terminal San José   ₡0.00       01/01/2025  [Editar]
  La Sabana           ₡300.00     01/01/2025  [Editar]
  Paseo Colón         ₡500.00     01/01/2025  [Editar]
  Tres Ríos           ₡650.00     01/01/2025  [Editar]
  Cartago             ₡800.00     01/01/2025  [Editar]
─────────────────────────────────────────────
```

---


---


**HU-10 - Gestionar Flota de Buses**

> Como administrador de empresa, quiero registrar, consultar, editar y desactivar los buses de mi flota para mantener la información de mis unidades actualizada en el sistema.


**Descripción:**

El administrador de la empresa puede gestionar los buses de su flota desde un panel interno. Puede registrar nuevos buses, editar su información descriptiva, cambiar su estado y consultarlos. 

**Supuestos:**

- Solo el dueño y los administradores de la empresa pueden gestionar su flota. No pueden ver ni modificar buses de otras empresas.

- Los campos modificables son: placa, número interno, rampa de accesibilidad y estado.

- No se puede cambiar el estado de un bus a INACTIVE o MAINTENANCE si tiene un viaje con estado IN_PROGRESS.

- La placa debe ser única en todo el sistema, no solo dentro de la empresa.

- El estado MAINTENANCE indica que el bus está temporalmente fuera de servicio pero no eliminado.

- El estado INACTIVE indica que el bus está fuera de servicio (puede ser eliminado o inactivo por otro motivo).

- No se eliminan buses físicamente del sistema para preservar el historial de viajes.


**Criterios de Aceptación:**

- **CA-01** — Al registrar un bus con todos los campos obligatorios válidos, el bus queda disponible en la flota de la empresa.
- **CA-02** — Al intentar registrar un bus con una placa ya existente en el sistema, se muestra un error y no se permite continuar.
- **CA-03** — Al dejar un campo obligatorio vacío en el formulario, el proceso no se completa y se indica el campo faltante.
- **CA-04** — Al editar la información de un bus, los cambios se reflejan de inmediato en el sistema.
- **CA-05** — Al intentar cambiar el estado de un bus a INACTIVE o MAINTENANCE mientras tiene un viaje en curso, el sistema lo impide e informa el motivo.
- **CA-06** — Al desactivar un bus, deja de aparecer en el mapa público pero permanece en el historial.
- **CA-07** — Al consultar la flota, el administrador ve todos los buses de su empresa con su estado actual.
- **CA-08** — El campo de rampa de accesibilidad debe ser visible para los usuarios públicos al consultar un viaje.


**Definition of Done:**


- [ ]  CRUD de buses implementado y funcional para dueño y administrador de empresa.
- [ ]  Validación de placa única en todo el sistema.
- [ ]  Bloqueo de cambio de estado implementado para buses con viaje en curso.
- [ ]  Los criterios CA-01 a CA-08 están cubiertos por pruebas automatizadas.
- [ ]  Interfaz funcional en móvil y escritorio.
- [ ]  Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```

─────────────────────────────────────────────
  ← Mi empresa — Flota de buses
─────────────────────────────────────────────
  [ + Agregar bus ]
─────────────────────────────────────────────
  Placa       N° interno  Rampa   Estado
  ──────────────────────────────────────────
  SJB-1234    #14         Sí      ACTIVE     [Editar]
  SJB-5678    #22         No      INACTIVE   [Editar]
  SJB-9999    #07         Sí      MAINTENANCE [Editar]
─────────────────────────────────────────────

```

```
─────────────────────────────────────────────
  Agregar / Editar bus
─────────────────────────────────────────────
  Placa *
  [ SJB-1234                           ]

  Número interno
  [ #14                                ]

  Rampa de accesibilidad
  ( ) Sí   (•) No

  Estado *
  [ ACTIVE              ▼ ]
─────────────────────────────────────────────
  [ Guardar ]
─────────────────────────────────────────────

```
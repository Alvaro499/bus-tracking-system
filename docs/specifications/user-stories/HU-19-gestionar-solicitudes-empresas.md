
---

**HU-19 — Gestionar Solicitudes de Registro de Empresas**

> Como administrador de plataforma, quiero revisar y aprobar o rechazar solicitudes de registro de empresas para evitar que información falsa sea publicada en el sistema.

**Descripción:**

El administrador de plataforma tiene acceso a un panel con todas las solicitudes de registro pendientes. Puede revisar los datos de cada empresa, aprobarla o rechazarla indicando un motivo. Al aprobar, la empresa queda activa y el dueño recibe sus credenciales de acceso. Al rechazar, el dueño recibe una notificación con el motivo.

**Supuestos:**

- Al aprobar una solicitud, el sistema activa la empresa (`status = ACTIVE`) y el usuario del dueño (`is_active = TRUE`) y genera y envía la contraseña provisional por correo.
- Al rechazar una solicitud, el motivo es obligatorio.
- Una solicitud rechazada puede volver a enviarse si el dueño corrige la información (a definir con el PO).
- Solo el administrador de plataforma puede aprobar o rechazar solicitudes.
- El administrador puede ver el historial de solicitudes aprobadas y rechazadas.

**Criterios de aceptación:**

- **CA-00** — La solicitud al crearse se mantiene con estado `PENDIENTE`.
- **CA-01** — Al ingresar al panel, el administrador ve la lista de solicitudes pendientes con los datos básicos de cada empresa.
- **CA-02** — Al revisar una solicitud, el administrador puede aprobarla o rechazarla.
- **CA-03** — Al rechazar una solicitud, el motivo es obligatorio y el dueño recibe una notificación con ese motivo.
- **CA-04** — Al aprobar una solicitud, la empresa queda activa y el dueño recibe sus credenciales por correo para iniciar sesión.
- **CA-05** — El administrador puede consultar el historial de solicitudes aprobadas y rechazadas.

**Definition of Done:**

- [ ] Panel de solicitudes pendientes implementado y funcional.
- [ ] Flujo de aprobación y rechazo implementado con notificaciones por correo.
- [ ] Los criterios CA-01 a CA-05 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```
─────────────────────────────────────────────
  Panel de administración — Solicitudes
─────────────────────────────────────────────
  Empresa               Cédula        Estado
  ──────────────────────────────────────────
  Autot. Moravia        3-101-123     PENDING  [Revisar]
  LUMACA SA             3-101-456     PENDING  [Revisar]
  Empresa Rechazada     3-101-789     REJECTED
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Solicitud — Autotransportes Moravia
─────────────────────────────────────────────
  Encargado:   Ramón Salazar
  Correo:      contacto@moravia.com
  Teléfono:    +506 2222-3333
  Cédula:      3-101-123456
─────────────────────────────────────────────
  [ Aprobar ]         [ Rechazar ]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Rechazar solicitud — Autot. Moravia
─────────────────────────────────────────────
  Motivo de rechazo *
  [ La cédula jurídica no corresponde  ]
  [ El ministerio de transportes no lo 
  reconoce como una empresa registrada ]

─────────────────────────────────────────────
  [ Confirmar rechazo ]
─────────────────────────────────────────────
```

---
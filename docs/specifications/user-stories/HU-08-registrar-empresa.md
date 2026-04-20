
---

**HU-08 — Registrar empresa en la plataforma**

> Como dueño de una empresa de buses, quiero registrar mi empresa en la plataforma para poder gestionar mis buses, rutas paradas y horarios.

**Descripción:**

El dueño de una empresa ingresa a la plataforma y completa un formulario de registro con los datos de su empresa. El proceso tiene tres etapas: llenar el formulario, verificar el correo electrónico, y esperar la aprobación del administrador global. Solo tras la aprobación puede acceder al panel de gestión.

**Supuestos:**

- El registro lo realiza únicamente el dueño de la empresa, no un administrador de ella.
- Un correo electrónico solo puede estar asociado a una cuenta.
- La aprobación la realiza el administrador global (ver HU-18).
- Si la solicitud es rechazada, el dueño recibe una notificación con el motivo (ver HU-18).
- Los datos mínimos del formulario los define el PO (nombre de empresa, correo, teléfono y cédula jurídica).

**Criterios de aceptación:**

- **CA-01** — Al completar el formulario con datos válidos y enviarlo, el sistema envía un correo de verificación al dueño.
- **CA-02** — Al intentar registrarse con un correo ya existente en el sistema, se muestra un mensaje de error y no se permite continuar.
- **CA-03** — Al verificar el correo, la cuenta queda en estado pendiente de aprobación por el administrador global.
- **CA-04** — Al ser aprobada la solicitud, el dueño recibe un correo con una contraseña generada automáticamente y puede iniciar sesión por primera vez.
- **CA-05** — Mientras la solicitud esté pendiente o rechazada, el dueño no puede acceder al panel de gestión.

**Definition of Done:**

- [ ] Formulario de registro implementado con validaciones de campos obligatorios.
- [ ] Flujo de verificación de correo funcionando correctamente.
- [ ] Los criterios CA-01 a CA-05 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.


**Mockup**

```

─────────────────────────────────────────────
  Registrar empresa
─────────────────────────────────────────────
  Nombre de encargado
  [ Ramón Salazar                      ]

  Nombre de la empresa
  [ Autotransportes Moravia SA         ]

  Correo electrónico
  [ contacto@moravia.com               ]

  Teléfono
  [ +506 2222-3333                     ]

  Cédula jurídica
  [ 3-101-123456                       ]

─────────────────────────────────────────────
  [ Registrar empresa ]
─────────────────────────────────────────────

```

---

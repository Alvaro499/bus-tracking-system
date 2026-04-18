
---

**HU-09 — Iniciar sesión como Usuario/Amin de Empresa**

> Como dueño de empresa o administrador global, quiero iniciar sesión en la plataforma para acceder a las funciones de gestión según mi rol.

**Descripción:**

El usuario ingresa su correo electrónico y recibe un magic link para acceder sin contraseña. El sistema lo redirige al panel correspondiente según su rol. Si la cuenta no está aprobada, se le informa el motivo.

**Supuestos:**

- La autenticación es por magic link, no por contraseña. El sistema envía un enlace al correo que expira después de N minutos (definir con el PO).
- Los roles posibles son: dueño de empresa, administrador de empresa y administrador global.
- Cada rol redirige a un panel distinto tras iniciar sesión.
- Una cuenta rechazada no puede iniciar sesión.

**Criterios de aceptación:**

- **CA-01** — Al ingresar un correo válido y registrado, el sistema envía un magic link al correo del usuario.
- **CA-02** — Al hacer clic en el magic link, el usuario accede al panel correspondiente a su rol.
- **CA-03** — Al ingresar un correo no registrado, se muestra un mensaje de error.
- **CA-04** — Al intentar iniciar sesión con una cuenta pendiente de aprobación, el sistema informa que la solicitud está en revisión.
- **CA-05** — Al intentar iniciar sesión con una cuenta rechazada, el sistema informa el motivo del rechazo.
- **CA-06** — El magic link expira tras N minutos y no puede reutilizarse.

**Definition of Done:**

- [ ] Flujo de magic link implementado y funcional.
- [ ] Redirección por rol funcionando correctamente.
- [ ] Los criterios CA-01 a CA-06 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

---

**Mockup:**

```
─────────────────────────────────────────
  Iniciar sesión
─────────────────────────────────────────
  Correo electrónico
  [ contacto@moravia.com               ]

─────────────────────────────────────────
  [ Enviar enlace de acceso ]
─────────────────────────────────────────
  Te enviaremos un enlace a tu correo.
```

---
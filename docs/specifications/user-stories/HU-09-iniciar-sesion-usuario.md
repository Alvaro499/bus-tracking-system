
---

**HU-09 — Iniciar sesión en la plataforma**

> El usuario ingresa su correo y contraseña. En el primer inicio de sesión el sistema le exige cambiar la contraseña generada automáticamente. Según su rol es redirigido al panel correspondiente.

**Descripción:**

El usuario ingresa su correo y contraseña. En el primer inicio de sesión el sistema le exige cambiar la contraseña generada automáticamente. Según su rol es redirigido al panel correspondiente.

**Supuestos:**

- La autenticación es por correo y contraseña.
- En el primer inicio de sesión el sistema obliga a cambiar la contraseña.
- Los roles posibles son: dueño de empresa (OWNER), administrador de empresa (ADMIN) y administrador de plataforma (PLATFORM_ADMIN).
- Cada rol redirige a un panel distinto tras iniciar sesión.
- Una cuenta rechazada no puede iniciar sesión.

**Criterios de aceptación:**

- **CA-01** — Al ingresar correo y contraseña válidos, el sistema redirige al usuario al panel correspondiente a su rol.
- **CA-02** — Al ingresar un correo no registrado o contraseña no registrada, se muestra un mensaje de error.
- **CA-03** — Al intentar iniciar sesión con una cuenta pendiente de aprobación o rechazada, el sistema informa un mensaje de error.

**Definition of Done:**

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

  Contraseña
  [ ••••••••••••                       ]

─────────────────────────────────────────
  [ Iniciar sesión ]
─────────────────────────────────────────

```

---
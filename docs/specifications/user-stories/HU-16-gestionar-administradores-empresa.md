
---

**HU-16 — Gestionar administradores de la empresa**

> Como dueño de empresa, quiero agregar y eliminar administradores de mi empresa para que puedan gestionar la información sin depender siempre de mí.

**Descripción:**

El dueño puede agregar administradores ingresando su correo. El sistema crea la cuenta automáticamente y genera una contraseña provisional. El dueño puede descargar la información del nuevo usuario en PDF o enviarla al correo del administrador creado. También puede eliminar administradores en cualquier momento, revocando su acceso inmediatamente.

**Supuestos:**

- Solo el dueño puede agregar y eliminar administradores de su empresa.
- No hay límite en la cantidad de administradores por empresa.
- Al agregar un administrador, el sistema crea su cuenta automáticamente con una contraseña provisional que deberá cambiar en su primer inicio de sesión.
- Si el correo ingresado ya existe en el sistema, no se crea una cuenta nueva sino que se asocia ese usuario existente a la empresa como administrador.
- Eliminar un administrador revoca su acceso a la empresa pero no elimina su cuenta del sistema.
- Un administrador eliminado puede volver a ser agregado en el futuro.

**Criterios de aceptación:**

- **CA-01** — Al ingresar un correo válido, el sistema crea la cuenta del administrador con una contraseña provisional y lo asocia a la empresa.
- **CA-02** — Al crear un administrador, el dueño puede descargar un PDF con los datos de acceso del nuevo usuario.
- **CA-03** — Al crear un administrador, el dueño puede enviar los datos de acceso al correo del nuevo usuario.
- **CA-04** — Al intentar agregar un correo ya asociado a esa empresa, el sistema lo impide e informa el motivo.
- **CA-05** — Al eliminar un administrador, pierde acceso a la empresa de inmediato.
- **CA-06** — Al consultar la lista de administradores, el dueño ve todos los administradores activos de su empresa.

**Definition of Done:**

- [ ] Flujo de creación de administrador con contraseña provisional implementado y funcional.
- [ ] Generación de PDF con datos de acceso implementada.
- [ ] Envío de datos de acceso por correo implementado.
- [ ] Los criterios CA-01 a CA-06 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```
─────────────────────────────────────────────
  ← Mi empresa — Administradores
─────────────────────────────────────────────
  [ + Agregar administrador ]
─────────────────────────────────────────────
  Nombre              Correo                   
  ──────────────────────────────────────────
  Juan Pérez          juan@moravia.com     [Eliminar]
  Ana Gómez           ana@moravia.com      [Eliminar]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Agregar administrador
─────────────────────────────────────────────
  Correo electrónico *
  [ juan@moravia.com                   ]

─────────────────────────────────────────────
  [ Crear administrador ]
─────────────────────────────────────────────
  ✓ Administrador creado
  Contraseña provisional: Xk9#mP2q

  [ Descargar PDF ]   [ Enviar por correo ]
─────────────────────────────────────────────
```

---

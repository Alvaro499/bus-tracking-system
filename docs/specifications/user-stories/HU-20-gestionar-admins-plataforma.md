
---

**HU-20 — Gestionar administradores de plataforma**

> Como administrador de plataforma, quiero agregar y eliminar otros administradores de plataforma para mantener el control del sistema entre personas de confianza.

**Descripción:**

El administrador de plataforma puede agregar nuevos administradores ingresando su correo. El sistema crea la cuenta automáticamente enviando por correo electrónico la contraseña a utilizar. También puede eliminar administradores existentes, revocando su acceso de inmediato. El sistema impide eliminar al último administrador activo para evitar que el sistema quede sin control.

**Supuestos:**

- Todos los administradores de plataforma tienen los mismos permisos, no hay jerarquía entre ellos.
- El primer administrador de plataforma se crea manualmente en la BD durante el despliegue inicial.
- Al agregar un administrador, el sistema crea su cuenta con `PLATFORM_ADMIN` y genera una contraseña que se envía por correo electrónico.
- No se puede eliminar al último administrador de plataforma activo.
- Eliminar un administrador desactiva su cuenta pero no la elimina físicamente.

**Criterios de aceptación:**

- **CA-01** — Al ingresar un correo válido, el sistema crea la cuenta del nuevo administrador con una contraseña provisional.
- **CA-02** — Al crear un administrador, el sistema muestra la contraseña provisional una única vez para que pueda compartirse.
- **CA-03** — Al intentar agregar un correo ya registrado en el sistema, se muestra un error y no se permite continuar.
- **CA-04** — Al eliminar un administrador, su acceso queda revocado de inmediato.
- **CA-05** — Al intentar eliminar al último administrador activo, el sistema lo impide e informa el motivo.
- **CA-06** — Al consultar la lista, el administrador ve todos los administradores de plataforma activos.

**Definition of Done:**

- [ ] Flujo de creación de administrador de plataforma con contraseña provisional implementado.
- [ ] Bloqueo de eliminación del último administrador activo implementado.
- [ ] Los criterios CA-01 a CA-06 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup:**

```
─────────────────────────────────────────────
  Panel — Administradores de plataforma
─────────────────────────────────────────────
  [ + Agregar administrador ]
─────────────────────────────────────────────
  Nombre         Correo                  
  ──────────────────────────────────────────
  Carlos Mora    cmora@plataforma.com    [Eliminar]
  Ana Solís      asolis@plataforma.com   [Eliminar]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Agregar administrador de plataforma
─────────────────────────────────────────────
  Correo electrónico *
  [ cmora@plataforma.com               ]

─────────────────────────────────────────────
  [ Crear administrador ]
─────────────────────────────────────────────
  ✓ Administrador creado
  Contraseña provisional: Xk9#mP2qTv

  [ Copiar ]   [ Cerrar ]
─────────────────────────────────────────────
```

---
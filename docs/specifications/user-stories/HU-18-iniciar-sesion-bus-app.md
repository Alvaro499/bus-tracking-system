
---

**HU-18 — Autenticar dispositivo del bus**

> Como administrador de empresa, quiero que cada dispositivo instalado en un bus se autentique con la placa y una contraseña para evitar que dispositivos externos envíen ubicaciones falsas al sistema.

**Descripción:**

Cada bus tiene una contraseña generada por el sistema que se combina con su placa para autenticarse. El técnico las ingresa en la app del bus una única vez al instalar el dispositivo. A partir de ese momento la app las usa automáticamente en cada transmisión sin intervención del chofer. Si las credenciales se comprometen, el administrador puede regenerar la contraseña.

**Supuestos:**

- Las credenciales son del bus, no del chofer. Cualquier chofer puede operar el bus sin afectar la autenticación.
- El identificador de autenticación es la placa del bus más una contraseña generada por el sistema.
- La contraseña se muestra una única vez al generarse. Si se pierde, debe regenerarse.
- Al regenerar la contraseña, la anterior queda inválida de inmediato.
- La configuración inicial de credenciales en la app la realiza el técnico o administrador, no el chofer.
- Esta autenticación aplica únicamente a la app del bus, no a la app pública de usuarios.

**Criterios de aceptación:**

- **CA-01** — Al generar credenciales para un bus, el sistema produce una contraseña única asociada a la placa de ese bus.
- **CA-02** — Al ingresar placa y contraseña válidas en la app del bus, el dispositivo queda autenticado y puede transmitir ubicación.
- **CA-03** — Al intentar autenticarse con credenciales inválidas, la app del bus no puede transmitir ubicación.
- **CA-04** — Al regenerar la contraseña de un bus, la anterior queda inválida de inmediato.
- **CA-05** — Un chofer distinto puede operar el mismo bus sin necesidad de cambiar las credenciales del dispositivo.

**Definition of Done:**

- [ ] Generación y regeneración de contraseña por bus implementadas y funcionales.
- [ ] Validación de credenciales en el endpoint de recepción de ubicación implementada.
- [ ] Los criterios CA-01 a CA-05 están cubiertos por pruebas automatizadas.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup — panel de administrador:**

```
─────────────────────────────────────────────
  ← Flota — Bus #14  SJB-1234
─────────────────────────────────────────────
  Credenciales del dispositivo
  ──────────────────────────────────────────
  Usuario:    SJB-1234  (placa)
  Contraseña: generada el 01/01/2025

  [ Regenerar contraseña ]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Contraseña generada — Bus #14
  ─────────────────────────────────────────
  Guarda esta contraseña, no se mostrará
  nuevamente.
  ─────────────────────────────────────────
  Usuario:     SJB-1234
  Contraseña:  Xk9#mP2qTv

  [ Copiar ]   [ Descargar ]   [ Cerrar ]
─────────────────────────────────────────────
```

**Mockup — app del bus (primer uso):**

```
─────────────────────────────────────────────
  Configurar dispositivo
─────────────────────────────────────────────
  Placa del bus *
  [ SJB-1234                           ]

  Contraseña *
  [ ••••••••••                         ]

─────────────────────────────────────────────
  [ Autenticar dispositivo ]
─────────────────────────────────────────────
```

---


---


# Segunda Versión Unicamente con JWT (No API KEY)


**HU-18 — Autenticar dispositivo del bus**

> Como administrador de empresa, quiero que cada dispositivo instalado en un bus se autentique con la placa y una contraseña para obtener acceso seguro al sistema y poder transmitir ubicación.

**Descripción:**

Cada bus tiene una contraseña generada por el sistema asociada a su placa. El técnico las configura en la app del bus una única vez. La app las usa para obtener un JWT de corta duración que se renueva automáticamente. Ese JWT es el que viaja en cada transmisión de ubicación, no la contraseña. Si las credenciales se comprometen, el administrador puede regenerar la contraseña invalidando el acceso de inmediato.

**Supuestos:**

- Las credenciales son del bus, no del chofer. Cualquier chofer puede operarlo sin afectar la autenticación.
- El identificador es la placa y el secreto es la contraseña generada por el sistema.
- La contraseña se muestra una única vez al generarse. Si se pierde, debe regenerarse.
- La contraseña nunca viaja en cada transmisión, solo se usa para obtener el JWT inicial.
- El JWT tiene una duración corta (N minutos, a definir con el PO) y se renueva automáticamente.
- Al regenerar la contraseña, la anterior y cualquier JWT activo quedan inválidos de inmediato.
- La configuración inicial la realiza el técnico o administrador, no el chofer.
- Esta autenticación aplica únicamente a la app del bus, no a la app pública de usuarios.

**Criterios de aceptación:**

- **CA-01** — Al generar credenciales para un bus, el sistema produce una contraseña única asociada a su placa, visible una única vez.
- **CA-02** — Al configurar la app con placa y contraseña válidas, el dispositivo obtiene un JWT y puede transmitir ubicación.
- **CA-03** — Al intentar autenticarse con credenciales inválidas, la app no puede obtener un JWT ni transmitir ubicación.
- **CA-04** — El JWT se renueva automáticamente antes de expirar sin intervención del chofer.
- **CA-05** — Al regenerar la contraseña, la anterior y los JWT activos quedan inválidos de inmediato.
- **CA-06** — Un chofer distinto puede operar el mismo bus sin necesidad de reconfigurar las credenciales.

**Definition of Done:**

- [ ] Generación y regeneración de contraseña por bus implementadas y funcionales.
- [ ] Endpoint de obtención de JWT implementado y funcional.
- [ ] Renovación automática de JWT implementada en la app del bus.
- [ ] Invalidación inmediata al regenerar contraseña implementada.
- [ ] Tabla `bus_credential` creada con su lógica de generación y revocación.
- [ ] Los criterios CA-01 a CA-06 están cubiertos por pruebas automatizadas.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

**Mockup — panel de administrador:**

```
─────────────────────────────────────────────
  ← Flota — Bus #14  SJB-1234
─────────────────────────────────────────────
  Credenciales del dispositivo
  ──────────────────────────────────────────
  Placa:       SJB-1234
  Contraseña:  generada el 01/01/2025

  [ Regenerar contraseña ]
─────────────────────────────────────────────
```

```
─────────────────────────────────────────────
  Contraseña generada — Bus #14
  ─────────────────────────────────────────
  Guarda esta contraseña, no se mostrará
  nuevamente.
  ─────────────────────────────────────────
  Placa:        SJB-1234
  Contraseña:   Xk9#mP2qTv

  [ Copiar ]   [ Descargar ]   [ Cerrar ]
─────────────────────────────────────────────
```

**Mockup — app del bus (primer uso):**

```
─────────────────────────────────────────────
  Configurar dispositivo
─────────────────────────────────────────────
  Placa del bus *
  [ SJB-1234                           ]

  Contraseña *
  [ ••••••••••                         ]

─────────────────────────────────────────────
  [ Autenticar dispositivo ]
─────────────────────────────────────────────
```

---

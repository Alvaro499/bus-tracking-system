
## Épicas

|Épica|Descripción|
|---|---|
|E-01|Visualización pública de buses|
|E-02|Registro y autenticación|
|E-03|Gestión interna de la empresa|
|E-04|Seguimiento en tiempo real|
|E-05|Administración global del sistema|

---

## E-01 — Visualización pública


## E-02 — Registro y autenticación



## E-03 — Gestión interna de la empresa


## E-04 — Seguimiento en tiempo real


## E-05 — Administración global

---

**HU-18 — Aprobar o rechazar empresas**

> Como **administrador global**, quiero **revisar y aprobar o rechazar solicitudes de registro de empresas** para **evitar que cualquier persona publique información falsa en el sistema.**

**Criterios de aceptación:**

- **CA-01** — Given hay una solicitud pendiente / When la reviso / Then puedo aprobarla o rechazarla con una razón
- **CA-02** — Given apruebo una empresa / When el dueño inicia sesión / Then puede empezar a gestionar su información
- **CA-03** — Given rechazo una empresa / When el solicitante consulta su estado / Then ve el motivo del rechazo


---
   
**HU-20 — Gestionar administradores globales**

> Como **administrador global**, quiero **poder agregar o quitar otros administradores globales** para **mantener el control del sistema entre personas de confianza.**

**Criterios de aceptación:**

- **CA-01** — Given agrego un administrador global / When acepta la invitación / Then tiene acceso a todas las funciones de administración
- **CA-02** — Given elimino un administrador global / When intenta acceder / Then ya no tiene permisos


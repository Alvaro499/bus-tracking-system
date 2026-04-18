---

**HU-02 — Ver perfil de una empresa de buses**

> Como usuario público, quiero poder acceder al perfil de una empresa de buses desde los resultados de búsqueda para conocer información básica sobre ella antes o durante mi viaje.

**Descripción:**

El usuario no busca empresas directamente. En cambio, accede al perfil de una empresa desde tres puntos contextuales dentro del flujo normal: el listado de empresas al buscar una ruta, el encabezado de la página de viajes del día, y el tooltip del bus en el mapa. Al acceder, se abre una página con información básica de la empresa. No se requiere iniciar sesión.

**Supuestos:**

- No existe un buscador de empresas. El acceso al perfil es siempre contextual, desde donde la empresa ya aparece naturalmente.
- El perfil es de solo lectura para el usuario público.
- La información del perfil la gestiona el administrador de la empresa (ver E-03).
- Si una empresa no ha completado su perfil, se muestra igualmente la página con los datos disponibles.

**Criterios de aceptación:**

- **CA-01** - Al buscar una ruta, cada empresa del listado se muestra como una fila seleccionable. Al hacer clic en cualquier parte de la fila (excepto el ícono de información), el usuario accede a los viajes del día de esa empresa. Un ícono (⋮ o ⓘ) al lado derecho de cada fila lleva a su perfil.
- **CA-02** — En la página de viajes del día, el encabezado con el nombre de la empresa incluye un enlace a su perfil.
- **CA-03** — En el tooltip del bus en el mapa, el nombre de la empresa es un enlace que lleva a su perfil.
- **CA-04** — El perfil de la empresa muestra al menos: nombre, descripción y rutas que opera.
- **CA-05** — El usuario puede acceder al perfil sin iniciar sesión.
- **CA-06** — Desde el perfil, el usuario puede volver al punto desde donde llegó.

**Definition of Done:**

- [ ] Página de perfil de empresa implementada y accesible desde los tres puntos definidos en CA-01, CA-02 y CA-03.
- [ ] Los criterios CA-01 a CA-06 están cubiertos por pruebas automatizadas.
- [ ] Interfaz funcional en móvil y escritorio.
- [ ] Sin bugs críticos abiertos relacionados a esta HU.

---

**Mockup:**

```
-------------------------------------------------------------
|  Ruta seleccionada: San José → Cartago                    |
-------------------------------------------------------------

┌───────────────────────────────────────────────────────────┐
│  Empresas que operan esta ruta:                           │
├───────────────────────────────────────────────────────────┤
│                                                           │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  LUMACA                              [ ⋮ ]           │ │
│  │  (toda la fila es cliqueable → ver todas las rutas)  │ │
│  └─────────────────────────────────────────────────────┘  │
│                                                           │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  Autotransportes Moravia               [ ⓘ ]       │  │
│  │  (toda la fila es cliqueable → ver rutas del día)   │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                           │
└───────────────────────────────────────────────────────────┘

Al hacer clic en [⋮] o [ⓘ] → se abre el perfil de la empresa.
Al hacer clic en cualquier otro lugar de la fila → se muestran los viajes del día.
```
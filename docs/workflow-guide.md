# Estrategia de Ramas y Reglas de Trabajo

## 1. Estrategia de Ramas

### Estructura de ramas principales y secundarias

La estrategia de ramas utilizada se basa en **Git Flow**, lo cual permite una gestión ordenada del desarrollo del proyecto. Se definen las siguientes ramas:

#### Ramas principales:
- `main`: Contiene el código y documentos listos para producción.
- `develop`: Rama base para el desarrollo de nuevas funcionalidades, pruebas integradas y edición colaborativa.

#### Ramas secundarias:

- **Feature branches**: Para el desarrollo de nuevas funcionalidades.
- Las ramas deben seguir la convención:  
  `ft/{número}-{descripción-corta}`  

  - Ejemplo: `ft/01-req`, `ft/01-crear-presupuesto`
- **Bug branches**: Para corrección de errores detectados.
- Las ramas deben seguir la convención:  
  `bug/{número}-{descripción-corta}`  

  - Ejemplo: `bug/34-crear-presupuesto`
- **Hotfix branches**: Para corregir errores críticos directamente en producción.
- Las ramas deben seguir la convención:  
  `hotfix/{número}-{descripción-corta}`  
  - Ejemplo: `hotfix/123-fix-error`
- **Release branches**: Para preparar una versión estable antes de fusionar a `main`.
- Las ramas deben seguir la convención:  
  `release/{versión}`  
  - Ejemplo: `release/1.0.0`

#### Ramas de documentación:
Cuando se trabaja con documentos como manuales, especificaciones, flujos de trabajo, etc., se deben seguir estas reglas:

- Las ramas deben seguir la convención:  
  `doc/{número}-{descripción-corta}`  
  > Ejemplo: `doc/12-flujo-trabajo`



## 2. Reglas para la Creación y Fusión de Ramas

### Convenciones de nomenclatura

| Tipo de Rama    | Convención                              | Ejemplo                                       |
|-----------------|------------------------------------------|-----------------------------------------------|
| Feature         | `ft/{número}-{descripción-corta}` | `ft/01-lc-crear-presupuesto`                  |
| Bugfix          | `bug/{número}-{descripción-corta}`| `bug/34-lc-npe-crear-presupuesto`             |
| Hotfix          | `hotfix/{número}-{descripción-corta}`         | `hotfix/123-fix-error`                        |
| Release         | `release/{versión}`                     | `release/1.0.0`                               |
| Documentación   | `doc/{número}-{descripción-corta}`            | `doc/12-flujo-trabajo`                        |

### Pull Requests y revisiones

- Toda rama secundaria debe fusionarse mediante un **Pull Request (PR)** hacia `develop` o `main`.
- Requisitos para la fusión:
  - Al menos una **revisión y aprobación** de otro miembro del equipo.
  - No tener código sin uso.
  - Validación de pruebas y CI (cuando aplica).
  - En caso de **documentos**, además de la aprobación:
    - Se verifica claridad, completitud y buena redacción del contenido.
    - Solo puede ser fusionado a `main` si ha sido explícitamente aprobado por un miembro del equipo.

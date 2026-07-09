


# ============================================================================
# ACCEDER A LA APP
# ============================================================================

# API REST
http://localhost:8081

# Swagger UI (documentación interactiva)
http://localhost:8081/swagger-ui.html

# OpenAPI JSON
http://localhost:8081/v3/api-docs


# Documentar Proyecto
pnpm dlx repomix --include "src/main/java/com/bustracking/companies/**" --output companies-module.xml

pnpm dlx repomix --include "src/main/java/com/bustracking/tracking/**" --output tracking-module.xml

pnpm dlx repomix --include "src/main/java/com/bustracking/admin/**" --output admin-module.xml

pnpm dlx repomix --include "src/main/java/com/bustracking/shared/**" --output shared-module.xml

# Documentar Proyecto Frontend

## Carpeta `src` y archivos de configuración raíz
pnpm dlx repomix . --output frontend-completo.xml

## Carpeta `src` únicamente
pnpm dlx repomix . --include "src/**/*" --output solo-src.xml

## Carpeta test
pnpm dlx repomix --include "src/test/java/com/bustracking/**" --output test-module.xml

#### Módulo test-tracking
pnpm dlx repomix --include "src/test/java/com/bustracking/tracking/**" --output test-tracking-module.xml

#### Módulo test-shared
pnpm dlx repomix --include "src/test/java/com/bustracking/shared/**" --output test-shared-module.xml

#### Módulo test-resources
pnpm dlx repomix --include "src/test/resources/**" --output test-resources-module.xml
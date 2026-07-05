


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

# Documentar Proyecto Frontend

## Carpeta `src` y archivos de configuración raíz
pnpm dlx repomix . --output frontend-completo.xml

## Carpeta `src` únicamente
pnpm dlx repomix . --include "src/**/*" --output solo-src.xml

## Carpeta test
pnpm dlx repomix --include "src/test/java/com/bustracking/**" --output test-module.xml
# Situación Actual - Backend Spring Boot + PostgreSQL Docker

## Resumen
Spring Boot Backend (Spring Boot 4.0.4) intenta conectarse a PostgreSQL 16 en Docker. El problema inicial era que las variables de entorno no se resolvían correctamente en los placeholders de properties. Ya identificados y parcialmente resueltos los problemas de configuración de perfiles y naming de variables.

---

## Archivos Clave

### 1. `.env` (Docker Compose)
```
POSTGRES_USER=bustracking_user_dev
POSTGRES_PASSWORD=123
POSTGRES_DB=bustracking_db_dev
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
```
**Rol:** Docker Compose usa estas variables para inicializar PostgreSQL. **Sin sufijo** es requisito de la imagen oficial.

### 2. `application.properties` (Spring Boot Base)
```properties
spring.application.name=backend
spring.profiles.active=@profileActive@
```
**Rol:** Maven reemplaza `@profileActive@` con `dev` durante compilación (desde pom.xml).

### 3. `application-dev.properties` (Perfil Dev)
```properties
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```
**Rol:** Configuración específica dev. Los placeholders se resuelven desde variables PowerShell.
**NOTA:** Cambió de `${POSTGRES_HOST_DEV}` a `${POSTGRES_HOST}` para alinearse con `.env` sin sufijo.

### 4. `pom.xml` (Perfiles Maven)
```xml
<profiles>
  <profile>
    <id>dev</id>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <profileActive>dev</profileActive>
    </properties>
  </profile>
</profiles>
```
**Rol:** Define que `dev` es el perfil por defecto y reemplaza `@profileActive@` por `dev`.

### 5. `docker-compose.yml`
```yaml
services:
  postgres-dev:
    image: postgres:16-alpine
    container_name: bustracking-postgres-dev
    env_file: .env
    ports:
      - "5432:5432"
    volumes:
      - postgres_dev_data:/var/lib/postgresql/data
```
**Rol:** Levanta PostgreSQL con variables del `.env`. Lee solo nombres **sin sufijo**.

---

## Comandos Usados

### Correcto (CON sufijo _DEV):
```powershell
$env:POSTGRES_HOST_DEV="localhost"; $env:POSTGRES_PORT_DEV="5432"; $env:POSTGRES_DB_DEV="bustracking_db_dev"; $env:POSTGRES_USER_DEV="bustracking_user_dev"; $env:POSTGRES_PASSWORD_DEV="123"; mvn clean spring-boot:run
```
**Problema:** `application-dev.properties` busca `${POSTGRES_HOST}`, no `${POSTGRES_HOST_DEV}` → falla.

### Intentado (CON $env:SPRING_PROFILES_ACTIVE):
```powershell
$env:POSTGRES_HOST="localhost"; $env:POSTGRES_PORT="5432"; $env:POSTGRES_DB="bustracking_db_dev"; $env:POSTGRES_USER="bustracking_user_dev"; $env:POSTGRES_PASSWORD="123"; $env:SPRING_PROFILES_ACTIVE="dev"; mvn clean spring-boot:run
```
**Estado:** ❌ FALLA - Mismo error de placeholders sin resolver.
**Problema:** Aunque se agrega `$env:SPRING_PROFILES_ACTIVE="dev"`, Spring Boot aún NO carga `application-dev.properties`. Los placeholders `${POSTGRES_HOST}`, etc. se pasan literal a Hibernate → falla con "Unable to determine Dialect".

---

## Errores Sufridos

| Error | Causa | Solución |
|-------|-------|----------|
| `Unable to determine Dialect without JDBC metadata` | Perfil dev no activo, `application-dev.properties` no cargado | Maven reemplaza `@profileActive@` automáticamente, perfil se activa |
| `Unable to open JDBC Connection... password authentication failed` | Usuario no existe en PostgreSQL (variables con sufijo `_DEV` no reconocidas por Docker) | `.env` debe tener nombres SIN sufijo: `POSTGRES_USER`, no `POSTGRES_USER_DEV` |
| Placeholders no resueltos: `${POSTGRES_HOST_DEV}` pasado literal a Hibernate | Mismatch entre nombres de variables en PowerShell y placeholders en properties | Alineación: `.env` sin sufijo, PowerShell variables SIN sufijo, `application-dev.properties` placeholders SIN sufijo |

---

## Flujo Esperado (Correcto)

1. **PowerShell:** `$env:POSTGRES_HOST="localhost"` (crea variable)
2. **Docker:** `docker-compose up -d` (lee `.env`, crea usuario `bustracking_user_dev`)
3. **Maven:** `mvn clean` (compila, reemplaza `@profileActive@` → `dev` en `application.properties`)
4. **Spring Boot:** Inicia, ve `spring.profiles.active=dev`, carga `application-dev.properties`
5. **Resolución:** Spring lee `${POSTGRES_HOST}`, busca variable PowerShell, reemplaza con `localhost`
6. **Conexión:** ✅ Conecta exitosamente a PostgreSQL

---

## Estado Actual

- ✅ Docker Compose levanta PostgreSQL correctamente
- ✅ Maven compila y reemplaza perfiles
- ❓ **Pendiente:** Ejecutar comando SIN sufijo `_DEV` para verificar que funciona
- ⚠️ `application-dev.properties` fue actualizado a placeholders sin sufijo (verificar líneas 13-16)

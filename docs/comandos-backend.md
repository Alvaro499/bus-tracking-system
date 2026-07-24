
# ============================================================================
# CORRER LA APP
# ============================================================================

# ✅ RECOMENDADO: Con el script (simple)
./run-dev.ps1

# ❌ Forma tediosa (sin script)
$env:POSTGRES_HOST = "localhost"
$env:POSTGRES_HOST_PORT = "5433"
$env:POSTGRES_DB = "bustracking_db_dev"
$env:POSTGRES_USER = "bustracking_user_dev"
$env:POSTGRES_PASSWORD = "123"
mvn clean spring-boot:run


# ============================================================================
# DOCKER - CONTENEDOR POSTGRESQL
# ============================================================================

#### 1. Levantar contenedor (crea si no existe)
docker-compose up -d

#### 2. Levantar mostrando logs en vivo
docker-compose up

#### 3. Ver logs en vivo
docker-compose logs -f postgres-dev

#### 4. Eliminar contenedor (borra datos)
docker-compose down

#### 5. Eliminar contenedor Y VOLUMEN (borra TODA la BD)
docker-compose down -v

#### 6. Eliminar también la imagen (para forzar recreación)
docker image rm postgres:16-alpine

#### 7. Revisar estado del contenedor
docker ps

#### 8. Detener solo el contenedor
docker stop bustracking-postgres-dev

#### 9. Reiniciar contenedor
docker restart bustracking-postgres-dev

#### 10. Ver logs para verificar que se ejecutó el script
docker-compose logs postgres-dev

#### 11. Ver logs específicos de un contenedor
docker logs container_name

#### 12. Historial de Contenedores
docker ps -a 


# ============================================================================
# PUERTOS - VER QIÉN USA QUÉ
# ============================================================================

# Ver todos los puertos en uso
netstat -an

# Ver puertos en uso + programas que los usan
Get-NetTCPConnection -State Listen | Select-Object LocalAddress, LocalPort, @{Name="Process";Expression={(Get-Process -Id $_.OwningProcess).ProcessName}}

# Ver específicamente quién usa puerto 5432
Get-NetTCPConnection -LocalPort 5432 -State Listen | ForEach-Object { $_.OwningProcess; Get-Process -Id $_.OwningProcess }

# Ver específicamente quién usa puerto 5433
Get-NetTCPConnection -LocalPort 5433 -State Listen | ForEach-Object { $_.OwningProcess; Get-Process -Id $_.OwningProcess }

# Ver específicamente quién usa puerto 8081 (Spring Boot)
Get-NetTCPConnection -LocalPort 8081 -State Listen | ForEach-Object { Get-Process -Id $_.OwningProcess }


# ============================================================================
# BD - COMANDOS DESDE TERMINAL (psql)
# ============================================================================

# CON DOCKER (RECOMENDADO)
docker exec -it bustracking-postgres-dev psql -U bustracking_user_dev -d bustracking_db_dev

# CON PSQL DIRECTO (necesita PostgreSQL instalado en Windows)
psql -h localhost -p 5433 -U bustracking_user_dev -d bustracking_db_dev


# Una vez conectado en psql (cualquiera de las opciones arriba):
\l                    # Listar todas las bases de datos
\dn                   # Listar todos los esquemas
\dt                   # Listar tablas en esquema public (vacío en nuestro caso)
\dt *.*               # Listar TODAS las tablas de TODOS los esquemas
\dt admin.*           # Listar tablas del esquema admin
\dt companies.*       # Listar tablas del esquema companies
\dt tracking.*        # Listar tablas del esquema tracking
\d+ tabla_name        # Ver estructura de una tabla
\d+ admin."user"      # Ver estructura de tabla en esquema específico
SELECT * FROM tabla;  # Query SQL
SELECT * FROM companies.schedule;  # Query en esquema específico
\q                    # Salir (o Ctrl+D)
Ctrl+D                # Salir sin comando

# EJECUTAR QUERY DIRECTO - DOCKER
docker exec -it bustracking-postgres-dev psql -U bustracking_user_dev -d bustracking_db_dev -c "SELECT * FROM companies.schedule;"

# EJECUTAR QUERY DIRECTO - PSQL
psql -h localhost -p 5433 -U bustracking_user_dev -d bustracking_db_dev -c "SELECT * FROM companies.schedule;"

# EJECUTAR ARCHIVO SQL - DOCKER
docker exec -it bustracking-postgres-dev psql -U bustracking_user_dev -d bustracking_db_dev -f /docker-entrypoint-initdb.d/01-init.sql

# EJECUTAR ARCHIVO SQL - PSQL
psql -h localhost -p 5433 -U bustracking_user_dev -d bustracking_db_dev -f init.sql


# ============================================================================
# MAVEN - ÚTILES
# ============================================================================

# Compilar sin correr
mvn clean compile

# Correr tests
mvn test

# Maven con stacktrace completo:
mvn test -e

# Maven con stacktrace completo y más detallado
mvn test -X
 --> Luego ejecuta: target/surefire-reports

# Correr solo los tests de integración
mvn verify

# Limpiar carpeta target/
mvn clean
# Bus Tracking System - Testing & CI/CD Context

## 📋 Project Overview

**Project Type:** Spring Boot 4.0.4 + Maven + PostgreSQL + Docker  
**Java Version:** 21  
**Package Structure:** DDD (Domain-Driven Design) with Bounded Contexts

```
backend/
├── src/main/java/com/bustracking/
│   ├── admin/            (Admin context)
│   ├── companies/        (Companies context - with Bus domain)
│   ├── shared/           (Shared kernel - exceptions, value objects)
│   └── tracking/         (Tracking context - with BusLocation domain)
├── src/test/java/com/bustracking/
│   ├── unit/             (Fast tests - no DB, no Docker)
│   │   ├── domain/       (Domain logic tests)
│   │   └── usecase/      (Use case tests)
│   ├── integration/      (Integration tests - with TestContainers)
│   │   ├── controller/   (WebMvcTest - mocked services)
│   │   └── repository/   (DataJpaTest + PostgreSQL TestContainers)
│   └── e2e/              (End-to-end tests)
├── pom.xml              (Maven dependencies)
├── docker-compose.yml   (Local dev PostgreSQL)
├── Dockerfile           (Production image - multi-stage build)
└── src/main/resources/
    ├── application.properties
    ├── application-dev.properties
    ├── application-test.properties
    └── db/init.sql
```

---

## 🧪 Testing Strategy

### **Test Types & Configuration**

| Test Type | Location | Seconds | TestContainer | DB | Base Class |
|-----------|----------|---------|----------------|----|----|
| **Unit - Domain** | `unit/domain/` | ~0.1s/test | ❌ | ❌ | None |
| **Unit - UseCase** | `unit/usecase/` | ~0.5s/test | ❌ | ❌ | None |
| **Controller** | `integration/controller/` | ~1s/test | ❌ | ❌ (Mocked) | `ControllerIntegrationTest` |
| **Repository** | `integration/repository/` | ~5-10s/test | ✅ | ✅ PostgreSQL | `RepositoryIntegrationTest` |
| **E2E** | `e2e/` | ~10-30s/test | ? | ? | ? |

### **Test Class Hierarchy**

**RepositoryIntegrationTest.java** (Testcontainers + PostgreSQL):
```java
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryIntegrationTest {
    @Container
    protected static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bustracking_db_test")
            .withUsername("bustracking_test_user")
            .withPassword("test_password_random");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

**ControllerIntegrationTest.java** (WebMvc - Mocked Services):
```java
@WebMvcTest
@ActiveProfiles("test")
public abstract class ControllerIntegrationTest {
    // No TestContainers - services are mocked
}
```

---

## 🔧 Maven Dependencies (Test)

```xml
<!-- DataJpaTest - Spring Boot 4.0 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- WebMvcTest - Spring Boot 4.0 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- TestContainers PostgreSQL -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.20.6</version>
    <scope>test</scope>
</dependency>
```

---

## 📊 Recent Refactoring (Value Objects)

Just refactored domain to extract Value Objects:

### **New Value Objects Created:**
1. **Plate** - Bus registration number (5-10 chars)
   - Location: `com.bustracking.companies.domain.valueobjects`
   - Test: `PlateTest` (12 tests)

2. **InternalNumber** - Optional bus internal ID (3-20 chars or null)
   - Location: `com.bustracking.companies.domain.valueobjects`
   - Test: `InternalNumberTest` (10 tests)

3. **BusLocation** - GPS snapshot at a point in time
   - Location: `com.bustracking.tracking.domain.model`
   - Test: `BusLocationTest` (9 tests + equals/hashCode)

### **Updated Aggregates:**
1. **Bus** - Refactored to use Plate & InternalNumber ValueObjects
   - Added: `createdAt` & `updatedAt` timestamp initialization
   - Added: Validation for `companyId` and `hasRamp`
   - Added: `updateTimestamp()` called on state changes
   - Test: `BusTest` (31 tests including timestamp validation)

### **Existing ValueObjects:**
- **GpsCoordinate** (shared) - Latitude/Longitude bounds validation
  - Test: `GpsCoordinateTest` (13 tests)

---

## 🚀 Commands for Testing

### **Local Development**
```bash
# Run all tests (fast + integration + e2e)
mvn test

# Run only fast tests (skip integration tests)
mvn test -DskipITs

# Run only domain unit tests
mvn test -Dtest=**/unit/domain/**

# Run only integration repository tests
mvn test -Dtest=**/integration/repository/**

# Run only controller tests
mvn test -Dtest=**/integration/controller/**

# Run specific test class
mvn test -Dtest=BusTest

# Run specific test method
mvn test -Dtest=BusTest#shouldCreateBusWithValidValues
```

### **Build & Verify (CI/CD Ready)**
```bash
# Full build with all tests
mvn clean verify

# Build for production
mvn clean package -DskipTests

# Build Docker image
docker build -t bustracking-backend:latest .

# Run Docker Compose (dev)
docker-compose up -d
docker-compose logs -f postgres-dev

# Run Docker Compose (test - if you create docker-compose.test.yml)
docker-compose -f docker-compose.test.yml up -d
```

---

## 🔄 CI/CD Pipeline Objectives

**Need to create:**

1. **GitHub Actions / GitLab CI Pipeline** that:
   - ✅ Runs fast tests (unit + controller) - ~30-60 seconds
   - ✅ Runs integration tests with TestContainers - ~5-10 minutes
   - ✅ Builds Docker image
   - ✅ (Optional) Pushes to Docker Registry
   - ✅ (Optional) Deploys to staging

2. **docker-compose.test.yml** (if needed for parallel test execution)

3. **Maven configuration** for CI/CD best practices

---

## 📁 Key Files for CI/CD Configuration

```
backend/
├── pom.xml                           ← Maven config (already has TestContainers)
├── Dockerfile                        ← Docker image (multi-stage build)
├── docker-compose.yml                ← Dev database only
├── src/main/resources/
│   ├── application-test.properties  ← Test profile config
│   └── db/init.sql                  ← Database init script
├── src/test/java/.../
│   └── testinfrastructure/
│       ├── ControllerIntegrationTest.java  ← WebMvcTest base
│       └── RepositoryIntegrationTest.java  ← DataJpaTest + TC base
│
├── CI-CD-CONTEXT.md                 ← This file
├── .github/workflows/                ← (TO CREATE) GitHub Actions
│   ├── test.yml                      ← Unit + Integration tests
│   ├── build.yml                     ← Build Docker image
│   └── deploy.yml                    ← Deploy to staging/prod
│
└── .gitlab-ci.yml                    ← (TO CREATE) GitLab CI (alternative)
```

---

## 🎯 What to Ask the Other AI

**When setting up CI/CD, ask for:**

1. **GitHub Actions Workflow** (or GitLab CI equivalent):
   - Fast tests (unit + controller) run in parallel on PR
   - Integration tests run only on main branch
   - Docker image build after tests pass
   - Optional: Push to Docker Hub / GitHub Registry
   - Optional: Deploy to staging on tag

2. **Docker Compose for Testing** (if parallel execution needed):
   - PostgreSQL with same config as RepositoryIntegrationTest
   - Health check configuration
   - Network setup for multiple tests

3. **Maven Plugin Configuration** (optional enhancements):
   - Surefire plugin for unit tests
   - Failsafe plugin for integration tests
   - JaCoCo for code coverage
   - Checkstyle / SpotBugs for code quality

4. **Best Practices**:
   - How to handle secrets (DB passwords, Docker registry)
   - Test reporting and artifacts
   - Caching dependencies
   - Parallel test execution strategy

---

## 📝 Notes

- **TestContainers is already configured** for RepositoryIntegrationTest
- **ControllerIntegrationTest uses @WebMvcTest** - no real DB needed
- **Profile system working:**
  - `dev` = Local development (docker-compose)
  - `test` = Unit + integration tests with TestContainers
  - `prod` = Production deployment
- **SQL Initialization:** `init.sql` runs automatically in test profile
- **Java 21** - remember in CI/CD setup
- **Maven 3.9+** recommended

---

## ✅ Test Summary (After Refactoring)

| Test Class | Tests | Status |
|-----------|-------|--------|
| PlateTest | 12 | ✅ New |
| InternalNumberTest | 10 | ✅ New |
| BusTest | 31 | ✅ Updated (timestamp + validation) |
| BusLocationTest | 9 | ✅ Updated (equals/hashCode) |
| GpsCoordinateTest | 13 | ✅ Existing |
| **TOTAL** | **75** | ✅ All ready |

**Ready for:** CI/CD pipeline creation

---

# 📄 COMPLETE PROJECT FILES

## 1️⃣ pom.xml (Excerpt - Dependencies & Profiles)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>4.0.4</version>
		<relativePath/>
	</parent>
	<groupId>com.bustracking</groupId>
	<artifactId>backend</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name/>
	<description/>

	<properties>
		<java.version>21</java.version>
	</properties>

	<dependencies>
		<!-- Spring Boot Core -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<!-- Dev Tools -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>

		<!-- Database -->
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>

		<!-- Lombok -->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>

		<!-- Testing - Spring Boot 4.0 separated these into own modules -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webmvc-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<!-- TestContainers - PostgreSQL for integration tests -->
		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>postgresql</artifactId>
			<version>1.20.6</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>testcontainers</artifactId>
			<version>1.20.6</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>junit-jupiter</artifactId>
			<version>1.20.6</version>
			<scope>test</scope>
		</dependency>

		<!-- Swagger/OpenAPI -->
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.8.3</version>
		</dependency>
	</dependencies>

	<!-- MAVEN PROFILES -->
	<profiles>
		<profile>
			<id>dev</id>
			<activation>
				<activeByDefault>true</activeByDefault>
			</activation>
			<properties>
				<build.profile.id>dev</build.profile.id>
				<profileActive>dev</profileActive>
			</properties>
		</profile>
		<profile>
			<id>prod</id>
			<activation>
				<activeByDefault>false</activeByDefault>
			</activation>
			<properties>
				<build.profile.id>prod</build.profile.id>
				<profileActive>prod</profileActive>
			</properties>
		</profile>
		<profile>
			<id>test</id>
			<activation>
				<activeByDefault>false</activeByDefault>
			</activation>
			<properties>
				<build.profile.id>test</build.profile.id>
				<profileActive>test</profileActive>
			</properties>
		</profile>
	</profiles>

	<build>
		<resources>
			<resource>
				<directory>src/main/resources</directory>
				<filtering>true</filtering>
				<excludes>
					<exclude>application-prod.properties</exclude>
				</excludes>
			</resource>
		</resources>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<annotationProcessorPaths>
						<path>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</path>
					</annotationProcessorPaths>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>
</project>
```

---

## 2️⃣ Application Properties Files

### **application.properties** (Profile Activation - Base)
**Location:** `src/main/resources/application.properties`

```properties
spring.application.name=backend

# PROFILE ACTIVATION - Resource filtering by Maven profile system
# mvn clean install         → dev (activeByDefault=true)
# mvn clean install -P prod → prod
spring.profiles.active=@profileActive@
```

---

### **application-dev.properties** (Development - Local Docker)
**Location:** `src/main/resources/application-dev.properties`

```properties
spring.application.name=backend
server.port=8080

# ============================================================================
# DATASOURCE (Local Development - Docker)
# ============================================================================
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST_DEV:localhost}:${POSTGRES_PORT_DEV:5432}/${POSTGRES_DB_DEV:bus_tracking_dev}
spring.datasource.username=${POSTGRES_USER_DEV:bus_dev_user}
spring.datasource.password=${POSTGRES_PASSWORD_DEV:dev_password_12345}
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate validates schema but doesn't create (schema is managed separately)
spring.jpa.hibernate.ddl-auto=validate

# ============================================================================
# LOGGING SQL (debugging queries)
# ============================================================================
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ============================================================================
# LOGGING (Verbose for development)
# ============================================================================
logging.level.root=INFO
logging.level.com.bustracking=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.boot.autoconfigure=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# ============================================================================
# DEVTOOLS (Auto-reload on code changes)
# ============================================================================
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# ============================================================================
# ERROR HANDLING (Developer-friendly stack traces)
# ============================================================================
server.error.include-stacktrace=always
server.error.include-binding-errors=always
server.error.include-message=always
server.error.include-exception=true
```

**Environment Variables Used (from .env):**
```
POSTGRES_HOST_DEV=localhost
POSTGRES_PORT_DEV=5432
POSTGRES_DB_DEV=bus_tracking_dev
POSTGRES_USER_DEV=bus_dev_user
POSTGRES_PASSWORD_DEV=dev_password_12345
```

---

### **application-test.properties** (Testing - TestContainers + PostgreSQL)
**Location:** `src/main/resources/application-test.properties`

```properties
spring.application.name=backend

# ============================================================================
# DATASOURCE (Test - TestContainers)
# ============================================================================
# TestContainers dynamically injects these at runtime
# - @DynamicPropertySource in RepositoryIntegrationTest
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST_TEST:localhost}:${POSTGRES_PORT_TEST:5433}/${POSTGRES_DB_TEST:bus_tracking_test}
spring.datasource.username=${POSTGRES_USER_TEST:bus_test_user}
spring.datasource.password=${POSTGRES_PASSWORD_TEST:test_password}
spring.datasource.driver-class-name=org.postgresql.Driver

# ============================================================================
# HIBERNATE (Testing)
# ============================================================================
# Don't auto-create schema - use SQL initialization instead
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# ============================================================================
# SQL INITIALIZATION (Schema + Data from SQL files)
# ============================================================================
# Always run initialization (CREATE TABLE, INSERT test data)
spring.sql.init.mode=always
spring.sql.init.platform=postgresql
# Schema initialization (CREATE TABLE statements)
spring.sql.init.schema-locations=classpath:db/test-init.sql
# Data initialization - empty here (we use @Sql fixtures in test classes)
spring.sql.init.data-locations=

# ============================================================================
# LOGGING (Minimal during tests - reduce noise)
# ============================================================================
logging.level.root=WARN
logging.level.com.bustracking=INFO

# ============================================================================
# SWAGGER (Enabled for e2e/functional tests)
# ============================================================================
springdoc.swagger-ui.enabled=true
springdoc.api-docs.enabled=true
```

---

### **application-prod.properties** (Production - Not included in dev build)
**Location:** `src/main/resources/application-prod.properties`

```properties
spring.application.name=backend
server.port=8080

# ============================================================================
# DATASOURCE (Production - External Database)
# ============================================================================
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST_PROD:prod-db.example.com}:${POSTGRES_PORT_PROD:5432}/${POSTGRES_DB_PROD:bus_tracking_prod}
spring.datasource.username=${POSTGRES_USER_PROD}
spring.datasource.password=${POSTGRES_PASSWORD_PROD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# ============================================================================
# HIBERNATE (Production)
# ============================================================================
# Validate only - schema is managed by migrations
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# ============================================================================
# LOGGING (Production - Minimal)
# ============================================================================
logging.level.root=WARN
logging.level.com.bustracking=INFO

# ============================================================================
# ERROR HANDLING (Production - Hide sensitive info)
# ============================================================================
server.error.include-stacktrace=never
server.error.include-binding-errors=never
server.error.include-message=never
server.error.include-exception=false
```

---

## 3️⃣ Dockerfile (Multi-stage Build)

**Location:** `backend/Dockerfile`

```dockerfile
# ============================================================================
# ETAPA 1: BUILD (Compilar el JAR)
# ============================================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar archivos de configuración
COPY pom.xml .

# Copiar código fuente
COPY src ./src

# Compilar el proyecto
RUN mvn clean package -DskipTests

# ============================================================================
# ETAPA 2: RUNTIME (Ejecutar el JAR)
# ============================================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar JAR compilado de la etapa anterior
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto (Spring Boot por defecto 8080)
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 4️⃣ docker-compose.yml (Local Development)

**Location:** `backend/docker-compose.yml`

```yaml
version: '3.8'

services:
  postgres-dev:
    image: postgres:16-alpine
    container_name: bustracking-postgres-dev
    
    env_file:
      - .env
    
    ports:
      - "5432:5432"
    
    volumes:
      - postgres_dev_data:/var/lib/postgresql/data
      - ./src/main/resources/db/init.sql:/docker-entrypoint-initdb.d/01-init.sql
    
    networks:
      - bustracking-network
    
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $${POSTGRES_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_dev_data:
    driver: local

networks:
  bustracking-network:
    driver: bridge
```

---

## 3️⃣ Dockerfile (Multi-Stage Build)

```dockerfile
# ============================================================================
# STAGE 1: BUILD (compile JAR)
# ============================================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ============================================================================
# STAGE 2: RUNTIME (execute JAR)
# ============================================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 4️⃣ application.properties

```properties
spring.application.name=backend

# PROFILE ACTIVATION - Resource filtering by Maven
# mvn clean install → dev (activeByDefault=true)
# mvn clean install -P prod → prod
spring.profiles.active=@profileActive@
```

---

## 5️⃣ application-test.properties

```properties
# APPLICATION PROPERTIES: TESTING PROFILE
# Active when: mvn test
# TestContainers: create using init.sql and spin up an ephemeral PostgreSQL per test class

spring.application.name=backend

# ============================================================================
# DATASOURCE (Test - TestContainers)
# ============================================================================
# TestContainers inyecta estos valores dinámicamente en IntegrationTest
spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST_TEST:localhost}:${POSTGRES_PORT_TEST:5433}/${POSTGRES_DB_TEST:bus_tracking_test}
spring.datasource.username=${POSTGRES_USER_TEST:bus_test_user}
spring.datasource.password=${POSTGRES_PASSWORD_TEST:test_password}
spring.datasource.driver-class-name=org.postgresql.Driver

# ============================================================================
# HIBERNATE (Testing)
# ============================================================================
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# ============================================================================
# SQL INITIALIZATION
# ============================================================================
spring.sql.init.mode=always
spring.sql.init-platform=postgresql
spring.sql.init.data-locations=classpath:db/init.sql

# ============================================================================
# LOGGING
# ============================================================================
logging.level.root=WARN
logging.level.com.bustracking=INFO

# ============================================================================
# SWAGGER
# ============================================================================
springdoc.swagger-ui.enabled=true
springdoc.api-docs.enabled=true
```

---

## 6️⃣ ControllerIntegrationTest.java

```java
package com.bustracking.shared.testinfrastructure;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;

// Base for tests of CONTROLLER (HTTP + Spring, mocked DB)
@WebMvcTest
@ActiveProfiles("test")
public abstract class ControllerIntegrationTest {

    // It doesn't need TestContainers because we mock the service layer
}
```

---

## 7️⃣ RepositoryIntegrationTest.java

```java
package com.bustracking.shared.testinfrastructure;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;

// Base para tests de REPOSITORY (solo DB, sin HTTP)
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class RepositoryIntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bustracking_db_test")
            .withUsername("bustracking_test_user")
            .withPassword("test_password_random");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

---

## 🚀 Ready for CI/CD AI Assistant

All configuration files above can be used as complete context for an AI assistant to create:
- GitHub Actions workflows
- GitLab CI pipelines
- Docker Compose for testing
- Maven plugin enhancements
- Deployment strategies

---

# 📚 TEST EXAMPLES (For CI/CD Configuration)

## Example 1️⃣ - UNIT TEST (Domain Logic)

**Location:** `src/test/java/com/bustracking/companies/unit/domain/BusTest.java`

```java
package com.bustracking.companies.unit.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.bustracking.companies.domain.enums.BusStatus;
import com.bustracking.companies.domain.model.Bus;
import com.bustracking.companies.domain.valueobjects.InternalNumber;
import com.bustracking.companies.domain.valueobjects.Plate;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ValidationException;

/**
 * UNIT TEST - Domain Logic (No DB, No Docker, No Mocking)
 * 
 * Speed: ~0.1 seconds per test
 * Database: NONE
 * Dependencies: Only domain objects and exceptions
 * 
 * Testing approach:
 * - Test business rules in isolation
 * - Test state transitions
 * - Test validation and error cases
 */
public class BusTest {
    
    // Common test data
    private final UUID validCompanyId = UUID.randomUUID();
    private final String validPlate = "CRC123";
    private final String validInternalNumber = "BUS-01";

    // =========================================================
    // Happy Path Tests
    // =========================================================

    @Test
    void shouldCreateBusWithValidValues() {
        // Arrange: Set up test data
        // No database connections needed
        
        // Act: Execute business logic
        Bus bus = new Bus(validCompanyId, validPlate, validInternalNumber, true);

        // Assert: Verify expected behavior
        assertEquals(validCompanyId, bus.getCompanyId());
        assertEquals(new Plate(validPlate), bus.getPlate());
        assertEquals(new InternalNumber(validInternalNumber), bus.getInternalNumber());
        assertTrue(bus.getHasRamp());
        assertEquals(BusStatus.INACTIVE, bus.getStatus());
        assertNotNull(bus.getId());
    }

    @Test
    void shouldCreateBusWithoutInternalNumber() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);

        assertNull(bus.getInternalNumber().getValue());
        assertEquals(BusStatus.INACTIVE, bus.getStatus());
    }

    // =========================================================
    // State Transitions - activate
    // =========================================================

    @Test
    void shouldActivateBusWhenInactive() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        assertEquals(BusStatus.ACTIVE, bus.getStatus());
    }

    @Test
    void shouldThrowWhenActivatingAlreadyActiveBus() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();

        // Business rule violation: cannot activate already active bus
        assertThrows(BusinessRuleException.class, bus::activate);
    }

    @Test
    void shouldThrowWhenActivatingBusInMaintenance() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        bus.sendToMaintenance();

        // Business rule violation: cannot activate bus in maintenance
        assertThrows(BusinessRuleException.class, bus::activate);
    }

    // =========================================================
    // State Transitions - deactivate
    // =========================================================

    @Test
    void shouldDeactivateBusWhenActive() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);
        bus.activate();
        bus.deactivate();

        assertEquals(BusStatus.INACTIVE, bus.getStatus());
    }

    @Test
    void shouldThrowWhenDeactivatingAlreadyInactiveBus() {
        Bus bus = new Bus(validCompanyId, validPlate, null, false);

        assertThrows(BusinessRuleException.class, bus::deactivate);
    }
}
```

**Key characteristics:**
- ✅ No database
- ✅ No Docker
- ✅ No Spring Context
- ✅ Plain Java testing with JUnit 5
- ✅ Fast: ~0.1 seconds
- ✅ Test business rules only

---

## Example 2️⃣ - CONTROLLER TEST (Integration - WebMvcTest)

**Location:** `src/test/java/com/bustracking/tracking/integration/controller/BusLocationControllerTest.java`

```java
package com.bustracking.tracking.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.testinfrastructure.ControllerIntegrationTest;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.application.usecase.GetBusLocationUseCase;
import com.bustracking.tracking.application.usecase.UpdateBusLocationUseCase;
import com.bustracking.tracking.domain.model.BusLocation;

/**
 * CONTROLLER TEST - HTTP Layer (Spring WebMvc + Mocked Services)
 * 
 * Speed: ~1 second per test
 * Database: NONE (services are mocked)
 * Dependencies: Spring Web, Mockito
 * 
 * Testing approach:
 * - Test HTTP request/response mapping
 * - Test status codes and error handling
 * - Test parameter validation
 * - Mock service layer (don't test business logic here)
 */
class BusLocationControllerTest extends ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Mock service layer - services are NOT tested here
    @MockitoBean
    private GetBusLocationUseCase getBusLocationUseCase;

    @MockitoBean
    private UpdateBusLocationUseCase updateBusLocationUseCase;

    // Test data
    private final UUID validBusId = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private final BigDecimal validLat = new BigDecimal("9.934739");
    private final BigDecimal validLng = new BigDecimal("-84.087502");
    private final LocalDateTime validTimestamp = LocalDateTime.of(2025, 1, 1, 12, 0, 0);

    private final BusLocation validBusLocation = new BusLocation(
        validBusId,
        new GpsCoordinate(validLat, validLng),
        validTimestamp
    );

    // =========================================================
    // GET /tracking/buses/{busId}/location - Happy Path
    // =========================================================

    @Test
    void shouldReturnBusLocationWhenBusExists() throws Exception {
        // Arrange
        when(getBusLocationUseCase.execute(validBusId))
            .thenReturn(validBusLocation);

        // Act & Assert: Test HTTP response
        mockMvc.perform(get("/tracking/buses/{busId}/location", validBusId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.busId").value(validBusId.toString()))
            .andExpect(jsonPath("$.lat").value(9.934739))
            .andExpect(jsonPath("$.lng").value(-84.087502))
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    // =========================================================
    // GET /tracking/buses/{busId}/location - Error Cases
    // =========================================================

    @Test
    void shouldReturn404WhenBusDoesNotExist() throws Exception {
        // Arrange: Mock service to throw exception
        when(getBusLocationUseCase.execute(validBusId))
            .thenThrow(new NotFoundException(
                ErrorCode.BUS_NOT_FOUND,
                "Bus not found",
                "Bus with ID " + validBusId + " does not exist"
            ));

        // Act & Assert: Verify HTTP 404 error
        mockMvc.perform(get("/tracking/buses/{busId}/location", validBusId))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenBusIdIsNotValidUUID() throws Exception {
        // Act & Assert: Test parameter validation
        mockMvc.perform(get("/tracking/buses/{busId}/location", "not-a-valid-uuid"))
            .andExpect(status().isBadRequest());
    }

    // =========================================================
    // POST /tracking/buses/{busId}/location - Happy Path
    // =========================================================

    @Test
    void shouldUpdateBusLocationWhenValidDataProvided() throws Exception {
        // Arrange
        String requestBody = objectMapper.writeValueAsString(
            new Object() {
                public final BigDecimal lat = new BigDecimal("9.934739");
                public final BigDecimal lng = new BigDecimal("-84.087502");
            }
        );

        doNothing().when(updateBusLocationUseCase)
            .execute(eq(validBusId), any(), any());

        // Act & Assert
        mockMvc.perform(post("/tracking/buses/{busId}/location", validBusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());

        // Verify method was called
        verify(updateBusLocationUseCase, times(1))
            .execute(eq(validBusId), any(), any());
    }

    // =========================================================
    // POST /tracking/buses/{busId}/location - Error Cases
    // =========================================================

    @Test
    void shouldReturn404WhenPostingLocationForNonExistentBus() throws Exception {
        // Arrange
        String requestBody = objectMapper.writeValueAsString(
            new Object() {
                public final BigDecimal lat = new BigDecimal("9.934739");
                public final BigDecimal lng = new BigDecimal("-84.087502");
            }
        );

        doThrow(new NotFoundException(
                ErrorCode.BUS_NOT_FOUND,
                "Bus not found",
                "Bus with ID " + validBusId + " does not exist"
            ))
            .when(updateBusLocationUseCase)
            .execute(eq(validBusId), any(), any());

        // Act & Assert
        mockMvc.perform(post("/tracking/buses/{busId}/location", validBusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isNotFound());
    }
}
```

**Key characteristics:**
- ✅ Spring Web Context (HTTP layer only)
- ✅ Services are MOCKED
- ✅ No database
- ✅ No TestContainers
- ✅ Tests HTTP request/response
- ✅ Fast: ~1 second
- ✅ Base class: `ControllerIntegrationTest`

---

## Example 3️⃣ - REPOSITORY TEST (Integration - DataJpaTest + TestContainers)

**Location:** `src/test/java/com/bustracking/tracking/integration/repository/BusLocationRepositoryTest.java`

```java
package com.bustracking.tracking.integration.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.bustracking.shared.testinfrastructure.RepositoryIntegrationTest;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.infrastructure.persistence.repository.BusLocationRepositoryImpl;
import com.bustracking.tracking.infrastructure.persistence.repository.BusLocationJpaRepository;

/**
 * REPOSITORY TEST - Persistence Layer (Real PostgreSQL + TestContainers)
 * 
 * Speed: ~5-10 seconds per test (includes container startup)
 * Database: REAL PostgreSQL (via TestContainers)
 * Dependencies: Spring Data JPA, TestContainers, PostgreSQL
 * 
 * Testing approach:
 * - Test persistence behavior (INSERT, UPDATE, SELECT)
 * - Test constraint validation
 * - Test mapping (Entity ↔ Model)
 * - Use SQL fixtures for test data
 * - Transactions ROLLBACK after each test
 */
@Sql({
    "/test-data/fixtures-shared.sql",          // Load companies + buses
    "/test-data/tracking-fixtures.sql"         // Load tracking-specific data
})
class BusLocationRepositoryTest extends RepositoryIntegrationTest {

    @Autowired
    private BusLocationJpaRepository busLocationJpaRepository;

    private BusLocationRepositoryImpl repository;
    
    // Fixed UUIDs from SQL fixtures
    private static final UUID BUS_ID_1 = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final UUID BUS_ID_2 = UUID.fromString("650e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        repository = new BusLocationRepositoryImpl(busLocationJpaRepository);
        // Test data already loaded from @Sql annotations
    }

    // =========================================================
    // Save (INSERT)
    // =========================================================

    @Test
    void shouldSaveBusLocationWhenItDoesNotExist() {
        // Arrange: Create test object
        GpsCoordinate coordinate = new GpsCoordinate(
            new BigDecimal("9.934739"),
            new BigDecimal("-84.087502")
        );
        BusLocation location = new BusLocation(BUS_ID_1, coordinate, LocalDateTime.now());

        // Act: Save to real database
        repository.save(location);

        // Assert: Query database and verify
        Optional<BusLocation> found = repository.findByBusId(BUS_ID_1);
        assertTrue(found.isPresent());
        assertEquals(BUS_ID_1, found.get().getBusId());
        assertEquals(coordinate.getLat(), found.get().getGpsCoordinate().getLat());
        assertEquals(coordinate.getLng(), found.get().getGpsCoordinate().getLng());
    }

    // =========================================================
    // Save (UPSERT - update existing)
    // =========================================================

    @Test
    void shouldUpdateBusLocationWhenItAlreadyExists() {
        // Arrange: Insert first location
        BusLocation firstLocation = new BusLocation(
            BUS_ID_2,
            new GpsCoordinate(new BigDecimal("9.934739"), new BigDecimal("-84.087502")),
            LocalDateTime.now()
        );
        repository.save(firstLocation);

        // Update with second location
        GpsCoordinate newCoordinate = new GpsCoordinate(
            new BigDecimal("10.000000"),
            new BigDecimal("-85.000000")
        );
        BusLocation updatedLocation = new BusLocation(BUS_ID_2, newCoordinate, LocalDateTime.now());

        // Act: Update in database
        repository.save(updatedLocation);

        // Assert: Verify update
        Optional<BusLocation> found = repository.findByBusId(BUS_ID_2);
        assertTrue(found.isPresent());
        assertEquals(newCoordinate.getLat(), found.get().getGpsCoordinate().getLat());
        assertEquals(newCoordinate.getLng(), found.get().getGpsCoordinate().getLng());
    }

    // =========================================================
    // FindByBusId (non-existent bus)
    // =========================================================

    @Test
    void shouldReturnEmptyForNonExistentBus() {
        // Arrange
        UUID nonExistentBusId = UUID.randomUUID();

        // Act
        Optional<BusLocation> found = repository.findByBusId(nonExistentBusId);

        // Assert
        assertTrue(found.isEmpty());
    }
}
```

**Key characteristics:**
- ✅ REAL PostgreSQL database (via TestContainers)
- ✅ Docker container automatically started
- ✅ SQL fixtures load test data from files
- ✅ Tests persistence behavior
- ✅ Transactions ROLLBACK after each test
- ✅ Slow: ~5-10 seconds (includes container overhead)
- ✅ Base class: `RepositoryIntegrationTest`

---

## 📊 Test Configuration Summary

| Type | Package | Annotations | DB | Speed | Purpose |
|------|---------|-------------|-------|-------|---------|
| **Unit (Domain)** | `unit/domain/` | `@Test` | ❌ | 0.1s | Test business logic |
| **Unit (UseCase)** | `unit/usecase/` | `@Test` | ❌ | 0.5s | Test application logic |
| **Controller** | `integration/controller/` | `@WebMvcTest` + `@Sql` | ❌ (Mocked) | 1s | Test HTTP layer |
| **Repository** | `integration/repository/` | `@DataJpaTest` + `@Testcontainers` | ✅ (Real) | 5-10s | Test persistence |
| **E2E** | `e2e/` | `@SpringBootTest` | ✅ (Real) | 10-30s | Full integration tests |

---

## 🔧 Running Tests in CI/CD

```bash
# Run all tests (unit + integration + e2e)
mvn clean verify

# Run only fast tests (skip integration with DB)
mvn clean test -DskipITs

# Run only unit tests
mvn clean test -Dtest=**/unit/**

# Run only controller tests
mvn clean test -Dtest=**/integration/controller/**

# Run only repository tests (with TestContainers)
mvn clean test -Dtest=**/integration/repository/**

# Run specific test class
mvn clean test -Dtest=BusLocationRepositoryTest

# Run specific test method
mvn clean test -Dtest=BusLocationRepositoryTest#shouldSaveBusLocationWhenItDoesNotExist

# Build Docker image (after successful tests)
docker build -t bustracking-backend:latest .

# Run Docker Compose locally
docker-compose up -d
docker-compose logs -f postgres-dev
```

---

## ✅ CI/CD Ready Checklist

- ✅ **Java 21** configured in `pom.xml`
- ✅ **Maven 3.9+** with proper plugin configuration
- ✅ **Spring Boot 4.0.4** with testing dependencies
- ✅ **TestContainers 1.20.6** for integration tests
- ✅ **PostgreSQL 16-alpine** for dev and test
- ✅ **Docker multi-stage build** for production
- ✅ **Maven profiles** for dev/test/prod configurations
- ✅ **SQL fixtures** for test data management
- ✅ **Test base classes** (`RepositoryIntegrationTest`, `ControllerIntegrationTest`)
- ✅ **Property files** for each environment
- ✅ **Test examples** for each test type (unit, controller, repository)


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

## 2️⃣ docker-compose.yml (Local Development)

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

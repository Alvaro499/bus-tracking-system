# ============================================================================
# DOCKERFILE: Contenerizar Spring Boot Backend
# ============================================================================
# Este archivo define cómo crear una imagen Docker para tu aplicación
#
# ¿QUÉ APRENDERÁS?
#   • FROM       = Imagen base (Sistema Operativo + Java)
#   • WORKDIR    = Carpeta de trabajo dentro del contenedor
#   • COPY       = Copiar archivos desde tu máquina hacia el contenedor
#   • RUN        = Ejecutar comandos (dentro del contenedor)
#   • ARG & ENV  = Variables (construcción y runtime)
#   • EXPOSE     = Qué puerto expone la aplicación
#   • ENTRYPOINT = Comando por defecto al iniciar
#
# ESTRUCTURA TÍPICA:
#   1. Etapa de Build     = Compilar el .jar
#   2. Etapa de Runtime   = Ejecutar el .jar
#
# ============================================================================

# ============================================================================
# ETAPA 1: BUILD (compilar el JAR)
# ============================================================================
# Usa Maven para compilar el proyecto
# Imagen base: eclipse-temurin:21-jdk (Java 21 + Maven)

FROM maven:3.9-eclipse-temurin-21 AS builder

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiar archivos de configuración del proyecto
# COPY <desde tu máquina> <hacia el contenedor>
COPY pom.xml .

# Copiar código fuente
COPY src ./src

# ¿QUÉ PASA AQUÍ?
# Maven descarga dependencias (puede ser lento la primera vez)
# Compila el código Java
# Genera un JAR en target/
RUN mvn clean package -DskipTests

# El JAR compilado queda en: /app/target/backend-0.0.1-SNAPSHOT.jar

# ============================================================================
# ETAPA 2: RUNTIME (ejecutar el JAR)
# ============================================================================
# Imagen más pequeña: solo el JRE (sin Maven)

FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar el JAR de la etapa de BUILD
# --from=builder indica que viene de la etapa anterior
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Puerto que expone la aplicación
# Spring Boot por defecto usa puerto 8080
EXPOSE 8080

# Comando al iniciar el contenedor
# Ejecuta: java -jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# ============================================================================
# NOTAS DE APRENDIZAJE
# ============================================================================
# Multi-stage build:
#   • builder    = Imagen pesada (con Maven, compilador, etc.) 
#   • final      = Imagen ligera (solo JRE, versión ejecutable)
#   • Ventaja: archivo Docker final es pequeño
#
# Variables de entorno (opcional):
#   • ENV JAVA_OPTS="-Xmx512m"  (Limitar memoria del JVM)
#
# Volúmenes (en docker-compose, no aquí):
#   • Logs
#   • Configuración externa
#   • Base de datos (NO en la imagen, en volumen)
#
# ============================================================================

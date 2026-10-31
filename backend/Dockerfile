# syntax=docker/dockerfile:1

# ==========================================
# Stage 1: Build the multi-module Maven application
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copy root and module POM files for dependency caching
COPY pom.xml ./
COPY core/pom.xml core/
COPY backend/pom.xml backend/

# Pre-fetch project dependencies in an isolated cache layer
RUN mvn -B -pl backend -am dependency:go-offline || true

# Copy source code of core and backend modules
COPY core/src core/src
COPY backend/src backend/src

# Build production JAR skipping test execution (CI runs automated tests)
RUN mvn -B clean package -pl backend -am -DskipTests

# ==========================================
# Stage 2: Minimal hardened runtime image
# ==========================================
FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

# Create a non-privileged system user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy executable fat JAR from the builder stage
COPY --from=builder /build/backend/target/battleship-backend-*.jar app.jar

# Set ownership to the non-privileged user
RUN chown -R appuser:appgroup /app

USER appuser

# Expose default HTTP port
EXPOSE 8080

# Configure container-aware JVM memory management and security entropy
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

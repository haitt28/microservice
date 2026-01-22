# ===================================================================
# Multi-stage Dockerfile for Spring Boot Microservice
# ===================================================================
# BEST PRACTICE #32: Optimized container build
# ===================================================================

# ==================== Build Stage ====================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom files first (for layer caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY common-lib/pom.xml common-lib/
COPY order-service/pom.xml order-service/

# Download dependencies (cached layer)
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# Copy source code
COPY common-lib/src common-lib/src
COPY order-service/src order-service/src

# Build the application
RUN ./mvnw clean package -pl order-service -am -DskipTests -B

# Extract layers for better caching
WORKDIR /app/order-service/target
RUN java -Djarmode=layertools -jar *.jar extract

# ==================== Runtime Stage ====================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Copy extracted layers (most stable first)
COPY --from=builder /app/order-service/target/dependencies/ ./
COPY --from=builder /app/order-service/target/spring-boot-loader/ ./
COPY --from=builder /app/order-service/target/snapshot-dependencies/ ./
COPY --from=builder /app/order-service/target/application/ ./

# JVM options for containers
ENV JAVA_OPTS="-XX:+UseZGC \
               -XX:+ZGenerational \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.backgroundpreinitializer.ignore=true"

EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

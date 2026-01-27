# ===================================================================
# Multi-stage Dockerfile for Spring Boot Microservice
# ===================================================================

# ==================== Build Stage ====================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY common-lib/pom.xml common-lib/
COPY analytics-service/pom.xml analytics-service/

# Download dependencies
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# Copy source code
COPY common-lib/src common-lib/src
COPY analytics-service/src analytics-service/src

# Build the application
RUN ./mvnw clean package -pl analytics-service -am -DskipTests -B

# Extract layers
WORKDIR /app/analytics-service/target
RUN java -Djarmode=layertools -jar *.jar extract

# ==================== Runtime Stage ====================
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Copy extracted layers
COPY --from=builder /app/analytics-service/target/dependencies/ ./
COPY --from=builder /app/analytics-service/target/spring-boot-loader/ ./
COPY --from=builder /app/analytics-service/target/snapshot-dependencies/ ./
COPY --from=builder /app/analytics-service/target/application/ ./

ENV JAVA_OPTS="-XX:+UseZGC \
    -XX:+ZGenerational \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.backgroundpreinitializer.ignore=true"

EXPOSE 8094

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

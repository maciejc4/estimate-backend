# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Runtime stage - use non-Alpine for embedded MongoDB compatibility (glibc required)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Add non-root user for security with home directory for embedded MongoDB
RUN groupadd -g 1001 appgroup && useradd -u 1001 -g appgroup -m -d /home/appuser appuser

# Install wget for healthcheck
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# Default to jwt profile, can be overridden via SPRING_PROFILES_ACTIVE env variable
ENV SPRING_PROFILES_ACTIVE=jwt

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

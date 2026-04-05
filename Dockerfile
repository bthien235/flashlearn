# ============================================
# Stage 1: Build
# ============================================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for caching dependencies)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application (skip tests for faster build)
RUN ./mvnw package -DskipTests -B

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Install curl for health check
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r flashlearn && useradd -r -g flashlearn flashlearn

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set ownership
RUN chown -R flashlearn:flashlearn /app

USER flashlearn

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
    CMD curl -f http://localhost:8080/login || exit 1

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

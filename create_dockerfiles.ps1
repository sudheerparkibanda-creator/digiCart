$dockerfileContent = @"
# Multi-stage build for Spring Boot service
FROM maven:3.8-openjdk-17-slim AS build
# Set working directory
WORKDIR /app
# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .
# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B
# Copy source code
COPY src src
# Build the application
RUN ./mvnw clean package -DskipTests
# Runtime stage
FROM openjdk:17-jre-slim
# Set working directory
WORKDIR /app
# Copy the JAR from build stage
COPY --from=build /app/target/*.jar app.jar
# Expose the port (will be overridden in docker-compose)
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
"@
$services = @('address-service', 'cart-service', 'notification-service', 'order-service', 'payment-service', 'price-service', 'stock-service', 'user-service')
foreach ($service in $services) {
    $dockerfileContent | Out-File -FilePath "$service\Dockerfile" -Encoding UTF8
}

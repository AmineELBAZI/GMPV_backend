# Use Eclipse Temurin JDK 21 as base image
FROM eclipse-temurin:21-jdk-alpine

# Set environment variables
ENV TZ=UTC \
    JAVA_OPTS=""

# Create app directory
WORKDIR /app

# Copy Maven/Gradle JAR build
COPY target/*.jar app.jar

# Expose port (default for Spring Boot)
EXPOSE 8081

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

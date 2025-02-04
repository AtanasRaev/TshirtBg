# Use an official OpenJDK runtime as a base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle/

# Copy the application source code
COPY src /app/src/

# Ensure the Gradle wrapper is executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew bootJar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "build/libs/TshirtBg-0.0.1-SNAPSHOT.jar"]

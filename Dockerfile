# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Rename the JAR file to a consistent name
RUN cp target/refugiservice-*.jar app.jar

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/app.jar app.jar

ENV ADMIN_PASSWORD=changeme
ENV JWT_SECRET=changeme
ENV UPLOAD_DIR=/data

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
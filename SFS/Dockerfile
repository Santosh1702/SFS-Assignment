# Stage 1: Build the Application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the JAR
RUN mvn clean package -DskipTests

# Stage 2: Create the Final Image
# Use a smaller, secure base image (J.R.E. only)
FROM eclipse-temurin:17-jre-focal
# The artifact name based on your pom.xml is SFS-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=target/SFS-0.0.1-SNAPSHOT.jar
COPY --from=build /app/${JAR_FILE} app.jar
EXPOSE 8080
# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "/app.jar"]
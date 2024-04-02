# Stage 1: Build the application
FROM gradle:8.5.0-jdk17 AS build

# Copy source code to the build stage
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Build the application
RUN gradle build --no-daemon -x intTest

# Stage 2: Run the application
FROM openjdk:17-alpine

# Copy the built JAR from the build stage to the run stage
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
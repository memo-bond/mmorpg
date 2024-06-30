# Use an official Gradle image to build the app
FROM gradle:8.8-jdk21 AS build

WORKDIR /home/gradle/project
  
# Copy the Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
  
# Copy the rest of the application source code
COPY app/src app/src
COPY app/build.gradle app/build.gradle
COPY settings.gradle settings.gradle
COPY models/proto /home/proto
  
  # Make the Gradle wrapper executable
RUN chmod +x gradlew
  
  # Build the application
RUN gradle clean build -PprotoPath=/home/proto
  
  # Use an official OpenJDK image to run the app
FROM openjdk:21-jdk-slim
  
  # Set the working directory inside the container
WORKDIR /app
  
  # Copy the built JAR file from the build stage
COPY --from=build /home/gradle/project/app/build/libs/*.jar mmorpg-game-server.jar
  
  # Expose the port your app runs on
EXPOSE 80
  
  # Set the entry point for the container
ENTRYPOINT ["java", "-jar", "mmorpg-game-server.jar", "prod"]

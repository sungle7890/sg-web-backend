# syntax=docker/dockerfile:1

# --- build stage: compile with JDK 25 (matches the Gradle toolchain) ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean bootJar --no-daemon

# --- run stage ---
FROM eclipse-temurin:25-jdk AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
# The platform injects $PORT; the app reads it via server.port=${PORT:...}
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

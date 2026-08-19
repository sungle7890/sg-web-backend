# syntax=docker/dockerfile:1

# --- build stage: compile with JDK 25 (matches the Gradle toolchain) ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean bootJar --no-daemon

# --- run stage ---
FROM eclipse-temurin:25-jdk AS runtime
WORKDIR /app

# Python + numpy for the /api/lottery predictor subprocess (Debian package, no pip build).
RUN apt-get update \
    && apt-get install -y --no-install-recommends python3 python3-numpy \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar /app/app.jar
# Predictor script + model (invoked via app.lottery.script-path=predictor/...)
COPY predictor ./predictor

# The platform injects $PORT; the app reads it via server.port=${PORT:...}
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

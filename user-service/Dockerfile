# Contexte de build attendu : la racine du repo (multi-module Gradle), pas ce dossier.
# docker build -f user-service/Dockerfile .

# --- Étape 1 : build (JDK complet + Gradle) ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle build.gradle ./
COPY user-service user-service
COPY mediatheque-service mediatheque-service
COPY event-service event-service

RUN ./gradlew :user-service:bootJar --no-daemon

# --- Étape 2 : exécution
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/user-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

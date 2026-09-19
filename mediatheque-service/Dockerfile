# Contexte de build attendu : la racine du repo (multi-module Gradle), pas ce dossier.
# docker build -f mediatheque-service/Dockerfile .

# --- Étape 1 : build (JDK complet + Gradle) ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle build.gradle ./
COPY user-service user-service
COPY mediatheque-service mediatheque-service
COPY event-service event-service

RUN ./gradlew :mediatheque-service:bootJar --no-daemon

# --- Étape 2 : exécution (JRE seul, image finale légère) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/mediatheque-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

# Byggfas
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY ./ ./

RUN ./gradlew clean build -x test

# Körfas
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/Project-manager-backend-0.0.1-SNAPSHOT.jar app.jar

COPY .env ./

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=5s CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
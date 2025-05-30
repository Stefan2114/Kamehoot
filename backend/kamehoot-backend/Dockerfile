# ---- Build Stage ----
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/kamehoot-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]


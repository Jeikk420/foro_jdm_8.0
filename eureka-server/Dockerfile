# Etapa 1: Construcción (Aquí Maven empaqueta tu proyecto en la nube)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (El chasis básico que ya tenías)
FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="Javier Ignacio"
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto del Gateway
EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
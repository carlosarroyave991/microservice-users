# Stage 1: Build de la aplicación con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copiamos el pom y las fuentes para aprovechar la cache de Docker
COPY pom.xml .
COPY src ./src
# Empaquetamos la aplicación (salteamos tests para agilizar el proceso, opcional)
RUN mvn clean package -DskipTests

# Stage 2: Imagen final para ejecutar la aplicación
FROM eclipse-temurin:17-jdk-jammy AS microproducts
WORKDIR /app
# Copiamos el JAR generado en el stage anterior
COPY --from=build /app/target/usuarios-0.0.1-SNAPSHOT.jar app.jar

# Se expone el puerto definido en las properties (server.port=8080)
EXPOSE 8080

# Se establece el comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
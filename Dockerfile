# Stage 1: Build de la aplicación con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copiamos el pom y las fuentes para aprovechar la cache de Docker
COPY pom.xml .
COPY src ./src
# Empaquetamos la aplicación (salteamos tests y flyway para el build)
RUN mvn clean package -DskipTests -Dflyway.skip=true

# Stage 2: Imagen final para ejecutar la aplicación
FROM eclipse-temurin:17-jdk-jammy AS microusers
WORKDIR /app
# Copiamos el JAR generado en el stage anterior
COPY --from=build /app/target/usuarios-0.0.1-SNAPSHOT.jar app.jar

# Variables de entorno requeridas
ENV SERVER_PORT=8080

ENV JWT_SECRET_KEY=07642a37f4961e4eabcaab480a8959a4714dfd31650e012d329b344574ea5687a3a68f2d9dfaacb231aa737a0791de60c8ba24c50705ab6e191b5fba4e3307935d9990f60e037f34f133fcfb5e56ba6182396ef7350fae79adb15b0e6f7883d8912471c1ea2384959c088a3a2bf3f1e97a22956546ffdd21ba8b47b2b8e20c75c37b7acf0365fc5fd82e0fbdb75f9fc3882933e74ec7055a16b9081c4f78563c541d33793b4d6e224c640c515c7fff53605c7868e7be1e9120f566a4a1b67ade164ee41603042a4cb38e34ffee6337c8a62d9716191429415072fd0226da07e867e9fbdf783c43a9416c7ff40def9673e5bb7409c2ebef4e5caf70f99aece063
ENV JWT_EXPIRATION=86400000
ENV JWT_REFRESH_TOKEN=604800000

# Se expone el puerto
EXPOSE ${SERVER_PORT}

# Se establece el comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
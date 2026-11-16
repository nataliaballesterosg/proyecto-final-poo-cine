# ---------------------------------------------------------------
# Etapa 1: compilar la aplicacion con Maven (multi-stage build)
# ---------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS compilacion
WORKDIR /fuente
# Primero solo el pom.xml para aprovechar la cache de dependencias
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# ---------------------------------------------------------------
# Etapa 2: imagen final, solo con el JRE y el .jar (mas liviana)
# ---------------------------------------------------------------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=compilacion /fuente/target/cine-api.jar app.jar
EXPOSE 8080
# La JVM usa como maximo el 75% de la memoria asignada al contenedor
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

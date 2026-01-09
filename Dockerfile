# Estágio de Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Define variáveis de ambiente para conectar ao banco do docker-compose
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/miniautorizador?createDatabaseIfNotExist=true
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
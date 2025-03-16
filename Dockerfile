# ETAPA 1 - Build con Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2 - Runtime con Java 21
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/crm-0.0.1-SNAPSHOT.jar /app/crm.jar

EXPOSE 9039
ENTRYPOINT ["java","-jar","/app/crm.jar"]
FROM maven:3.6.1-jdk-8-alpine AS build
WORKDIR /app
USER root
COPY pom.xml ./pom.xml
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

##
## Package stage
##
FROM openjdk:8-jdk-alpine
COPY --from=build /app/target/ptit.service.jar /usr/local/lib/ptit.service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/usr/local/lib/ptit.service.jar"]

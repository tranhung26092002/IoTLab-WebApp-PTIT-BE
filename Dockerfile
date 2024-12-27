# Sử dụng base image với OpenJDK 17
FROM openjdk:17-jdk-slim

# Set thư mục làm việc trong container
WORKDIR /app

# Copy file JAR của ứng dụng vào container
COPY target/*.jar app.jar

# Mở cổng 8761 cho Eureka service
EXPOSE 8761

# Chạy ứng dụng Eureka
ENTRYPOINT ["java", "-jar", "app.jar"]

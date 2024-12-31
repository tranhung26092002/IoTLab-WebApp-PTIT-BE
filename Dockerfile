# Sử dụng base image với OpenJDK 17
FROM openjdk:17-jdk-slim

# Set thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR vào thư mục /app trong container
# Giả sử rằng file JAR được tạo ra trong thư mục target với tên tương ứng với project
COPY target/notification-service.jar app.jar

# Mở cổng 8087 cho notification service
EXPOSE 8087

# Chạy ứng dụng Eureka
ENTRYPOINT ["java", "-jar", "app.jar"]

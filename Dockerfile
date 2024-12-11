# Stage 1: Gói ứng dụng Maven
FROM maven:3.6.1-jdk-8-alpine AS build

WORKDIR /app

# Sao chép file pom.xml vào thư mục làm việc của container
COPY pom.xml .

# Tải dependencies và lưu vào layer cache
RUN mvn dependency:go-offline

# Sao chép toàn bộ mã nguồn vào thư mục làm việc của container
COPY src ./src

# Gói ứng dụng, bỏ qua các bài kiểm tra (tests)
RUN mvn package -DskipTests

# Stage 2: Chạy ứng dụng từ file JAR đã được gói
FROM openjdk:10.0.2

WORKDIR /app

# Sao chép file JAR từ stage 1 vào thư mục làm việc của container
COPY --from=build /app/target/ecomerce-service.jar /usr/local/lib/ecomerce-service.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar","/usr/local/lib/ecomerce-service.jar"]

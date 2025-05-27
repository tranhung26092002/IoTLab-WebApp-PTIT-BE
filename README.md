# 🚀 IoT Lab WebApp Backend

> Đây là dự án backend cho IoT Lab WebApp, được xây dựng theo kiến trúc microservices sử dụng Spring Boot.

## 📋 Cấu trúc dự án

Dự án bao gồm các service chính sau:

| Service | Port | Mô tả |
|---------|------|--------|
| 🎯 Eureka Service | 8761 | Service Discovery cho toàn bộ hệ thống |
| 🌐 API Gateway | 8080 | Gateway để điều hướng các request |
| 👤 User Service | 8081 | Quản lý người dùng |
| 📱 Device Service | 8082 | Quản lý thiết bị IoT |
| 🔔 Notification Service | 8087 | Xử lý thông báo |
| 📡 MQTT Service | 8085 | Xử lý giao thức MQTT |
| 📚 Practice Service | 8084 | Quản lý bài thực hành |
| 💾 Storage Service | 8086 | Quản lý lưu trữ |

## ⚙️ Yêu cầu hệ thống

- Java 17 hoặc cao hơn
- Maven
- Docker và Docker Compose
- PostgreSQL 15
- RabbitMQ 3

## 🚀 Cách chạy dự án

### 1. Chạy thông thường (không dùng Docker)

#### Bước 1: Cấu hình môi trường
Tạo file `.env` trong thư mục gốc với các biến môi trường cần thiết:

```env
POSTGRES_DB=iotlab
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
```

#### Bước 2: Chạy dự án
Sử dụng các file shell script:

1. Cấp quyền thực thi cho các file:
```bash
chmod +x start.sh stop.sh
```

2. Chạy dự án:
```bash
./start.sh
```

3. Tắt dự án:
```bash
./stop.sh
```

### 2. Chạy với Docker Compose

#### Bước 1: Build và push Docker images
Sử dụng script `deploy-all.sh`:

1. Cấp quyền thực thi:
```bash
chmod +x deploy-all.sh
```

2. Chạy script:
```bash
./deploy-all.sh
```

Script này sẽ:
- Build JAR file cho mỗi service
- Build Docker image
- Push image lên DockerHub
- Pull các image mới nhất
- Khởi động hệ thống với Docker Compose

#### Bước 2: Chạy với Docker Compose
```bash
docker-compose up -d
```

Để tắt hệ thống:
```bash
docker-compose down
```

## 🔗 Truy cập các service

### Dashboard và Management UI
- 🎯 Eureka Dashboard: http://localhost:8761
- 🌐 API Gateway: http://localhost:8080
- 🐰 RabbitMQ Management: http://localhost:15672 (guest/guest)

### Swagger UI Documentation
Mỗi service đều có trang Swagger UI riêng để xem và test API:

| Service | Swagger UI URL |
|---------|----------------|
| User Service | http://localhost:8081/swagger-ui.html |
| Device Service | http://localhost:8082/swagger-ui.html |
| Notification Service | http://localhost:8087/swagger-ui.html |
| MQTT Service | http://localhost:8085/swagger-ui.html |
| Practice Service | http://localhost:8084/swagger-ui.html |
| Storage Service | http://localhost:8086/swagger-ui.html |

## ⚠️ Lưu ý quan trọng

1. Đảm bảo các port cần thiết không bị sử dụng bởi ứng dụng khác
2. Khi chạy thông thường, các service sẽ được khởi động tuần tự với thời gian chờ 30 giây giữa mỗi service
3. Khi sử dụng Docker, tất cả các service sẽ được khởi động đồng thời
4. Dữ liệu PostgreSQL sẽ được lưu trữ trong Docker volume

## 🔧 Troubleshooting

### 1. Service không khởi động được
- Kiểm tra logs của service đó
- Đảm bảo các biến môi trường đã được cấu hình đúng
- Kiểm tra kết nối đến các service phụ thuộc

### 2. Docker không chạy được
- Kiểm tra Docker daemon đã chạy
- Kiểm tra quyền truy cập Docker
- Xóa các container và image cũ nếu cần

### 3. Swagger UI không truy cập được
- Kiểm tra service đã khởi động thành công
- Kiểm tra port của service có đúng không
- Kiểm tra cấu hình Swagger trong application.yml của service

## 📝 Liên hệ

Nếu có bất kỳ vấn đề nào, vui lòng tạo issue trong repository.

---
Made with ❤️ by Hung Tran 
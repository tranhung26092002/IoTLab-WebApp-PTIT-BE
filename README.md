# 🚀 IoT Lab WebApp Backend

> Đây là dự án backend cho IoT Lab WebApp, được xây dựng theo kiến trúc microservices sử dụng Spring Boot.

## 📋 Cấu trúc dự án

Dự án bao gồm các service chính sau:

| Service             | Port | Mô tả                                  |
| ------------------- | ---- | -------------------------------------- |
| 🎯 Eureka Service   | 8761 | Service Discovery cho toàn bộ hệ thống |
| 🌐 API Gateway      | 8080 | Gateway để điều hướng các request      |
| 👤 User Service     | 8081 | Quản lý người dùng và xác thực         |
| 📱 Device Service   | 8082 | Quản lý thiết bị IoT                   |
| 📚 Practice Service | 8083 | Quản lý bài thực hành và bài thi       |
| 💾 Storage Service  | 8084 | Quản lý lưu trữ file                   |

## ⚙️ Yêu cầu hệ thống

- **Java 17** hoặc cao hơn
- **Maven 3.6+**
- **PostgreSQL 15** (tùy chọn - có thể chạy không cần DB)
- **RabbitMQ 3** (tùy chọn - đã được cấu hình để tắt)

## 🚀 Cách chạy dự án

### 1. Chạy thông thường (Local Development)

#### Bước 1: Cấu hình môi trường

Tạo file `.env` trong thư mục gốc với các biến môi trường cần thiết:

```env
# Database Configuration (Tùy chọn)
POSTGRES_DB=iotlab_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/iotlab_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# Eureka Configuration
EUREKA_URI=http://localhost:8761/eureka/

# RabbitMQ Configuration (Đã tắt để tránh lỗi kết nối)
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# Environment
ENVIRONMENT=development
DEBUG=false
ASYNC_CORE_POOL_SIZE=5

# JWT Configuration
JWT_SECRET=your-secret-key-here-make-it-long-and-secure
REFRESH_TOKEN_KEY=your-refresh-token-key-here

# Service URLs
USER_URL=http://localhost:8081/user
DEVICE_URL=http://localhost:8082/device
PRACTICE_URL=http://localhost:8083/practice
STORAGE_URL=http://localhost:8084/storage
```

#### Bước 2: Khởi động các service theo thứ tự

**1. Khởi động Eureka Service (Service Discovery):**

```bash
cd eureka-service
mvn spring-boot:run
```

**2. Khởi động API Gateway:**

```bash
cd api-gateway
mvn spring-boot:run
```

**3. Khởi động các service khác:**

```bash
# User Service
cd user-service
mvn spring-boot:run

# Device Service
cd device-service
mvn spring-boot:run

# Practice Service
cd practice-service
mvn spring-boot:run

# Storage Service
cd storage-service
mvn spring-boot:run
```

#### Bước 3: Sử dụng script tự động (Linux/Mac)

```bash
# Cấp quyền thực thi
chmod +x start.sh stop.sh restart.sh

# Khởi động tất cả services
./start.sh

# Dừng tất cả services
./stop.sh

# Khởi động lại
./restart.sh
```

### 2. Chạy với Docker Compose

#### Bước 1: Build và push Docker images

```bash
# Cấp quyền thực thi
chmod +x deploy-all.sh

# Build và deploy
./deploy-all.sh
```

#### Bước 2: Chạy với Docker Compose

```bash
# Khởi động tất cả services
docker-compose up -d

# Xem logs
docker-compose logs -f

# Dừng tất cả services
docker-compose down
```

## 🔗 Truy cập các service

### Dashboard và Management UI

- 🎯 **Eureka Dashboard**: http://localhost:8761
- 🌐 **API Gateway**: http://localhost:8080
- 🐰 **RabbitMQ Management**: http://localhost:15672 (guest/guest)

### Swagger UI Documentation

Mỗi service đều có trang Swagger UI riêng để xem và test API:

| Service          | Swagger UI URL                                       |
| ---------------- | ---------------------------------------------------- |
| User Service     | http://localhost:8081/user/swagger-ui/index.html     |
| Device Service   | http://localhost:8082/device/swagger-ui/index.html   |
| Practice Service | http://localhost:8083/practice/swagger-ui/index.html |
| Storage Service  | http://localhost:8084/storage/swagger-ui/index.html  |

### Health Check Endpoints

Kiểm tra trạng thái hoạt động của các service:

| Service             | Health Check URL                               |
| ------------------- | ---------------------------------------------- |
| 🎯 Eureka Service   | http://localhost:8761/actuator/health          |
| 🌐 API Gateway      | http://localhost:8080/actuator/health          |
| 👤 User Service     | http://localhost:8081/user/actuator/health     |
| 📱 Device Service   | http://localhost:8082/device/actuator/health   |
| 📚 Practice Service | http://localhost:8083/practice/actuator/health |
| 💾 Storage Service  | http://localhost:8084/storage/actuator/health  |

## ⚙️ Cấu hình đặc biệt

### RabbitMQ Configuration

Hệ thống đã được cấu hình để **tắt RabbitMQ** để tránh lỗi kết nối:

```yaml
rabbitmq:
  enabled: false # Tắt RabbitMQ để tránh lỗi kết nối
```

Nếu muốn sử dụng RabbitMQ:

1. Cài đặt và khởi động RabbitMQ
2. Thay đổi `rabbitmq.enabled: true` trong file `application.yml`
3. Khởi động lại các service

### Health Check Configuration

RabbitMQ health check đã được tắt:

```yaml
management:
  health:
    rabbit:
      enabled: false
    db:
      enabled: true
```

## 🔧 Troubleshooting

### 1. Lỗi kết nối Eureka

**Triệu chứng:** Service hiển thị trạng thái DOWN trong Eureka Dashboard

**Nguyên nhân:**

- Service chưa khởi động
- Lỗi kết nối RabbitMQ (đã được tắt)
- Lỗi kết nối Database

**Giải pháp:**

- Kiểm tra service đã khởi động chưa
- Kiểm tra logs của service
- Đảm bảo Eureka Service đang chạy trên port 8761

### 2. Lỗi RabbitMQ Connection

**Triệu chứng:**

```
Rabbit health check failed
Connection refused: no further information
```

**Giải pháp:**

- RabbitMQ đã được tắt trong cấu hình
- Nếu cần RabbitMQ, hãy cài đặt và khởi động RabbitMQ trước

### 3. Service không khởi động được

**Kiểm tra:**

- Java version: `java -version`
- Maven version: `mvn -version`
- Port có bị chiếm không: `netstat -an | findstr :8081`
- Logs của service

### 4. Database Connection Error

**Giải pháp:**

- Cài đặt PostgreSQL
- Cấu hình đúng thông tin kết nối trong `.env`
- Hoặc tắt database auto-configuration nếu không cần

## 📝 Cấu trúc thư mục

```
IoTLab-WebApp-PTIT-BE-develop/
├── eureka-service/          # Service Discovery
├── api-gateway/            # API Gateway
├── user-service/           # User Management
├── device-service/         # IoT Device Management
├── practice-service/       # Practice & Exam Management
├── storage-service/        # File Storage
├── esp/                    # ESP32 Code
├── docker-compose.yml      # Docker configuration
├── start.sh               # Start script
├── stop.sh                # Stop script
└── README.md              # This file
```

## 🚀 Deployment

### Production Deployment

1. **Build JAR files:**

```bash
mvn clean package -DskipTests
```

2. **Docker build:**

```bash
docker build -t your-registry/iotlab-service:latest .
```

3. **Deploy với Kubernetes hoặc Docker Swarm**

## 📞 Liên hệ

Nếu có bất kỳ vấn đề nào, vui lòng tạo issue trong repository.

---

Made with ❤️ by Hung Tran

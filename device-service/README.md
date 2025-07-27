# 📱 Device Service

> Microservice quản lý thiết bị IoT và thiết bị thường cho IoT Lab WebApp

## 📋 Mô tả

Device Service là một microservice chịu trách nhiệm quản lý:

- **Thiết bị IoT WiFi** (ESP32) với MQTT integration
- **Thiết bị thường** (laptop, máy tính, etc.)
- **Auto-registration** thiết bị IoT với Active Code
- **QR Code activation** để kích hoạt thiết bị
- **Thu thập dữ liệu real-time** từ sensors
- **Dashboard hiển thị** chỉ thiết bị đã active
- **Quản lý trạng thái và điều khiển** thiết bị từ xa
- **Lịch sử mượn/trả** thiết bị

## 🚀 Cách chạy

### Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- PostgreSQL 15 (tùy chọn)
- MQTT Broker (Mosquitto) - tùy chọn

### Khởi động service

```bash
# Clone repository (nếu chưa có)
cd device-service

# Build project
mvn clean install

# Chạy service
mvn spring-boot:run
```

### Cấu hình môi trường

Tạo file `.env` trong thư mục gốc với các biến:

```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/iotlab_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
DB_CONNECTION_TIMEOUT=30000
DB_MAX_POOL_SIZE=10
DB_MAX_LIFETIME=1800000

# Eureka Configuration
EUREKA_URI=http://localhost:8761/eureka/

# Environment
ENVIRONMENT=development
DEBUG=false
ASYNC_CORE_POOL_SIZE=5

# External Services
STORAGE_SERVICE=http://localhost:8084/storage

# MQTT Configuration (Optional)
MQTT_BROKER_URL=localhost:1883
MQTT_CLIENT_ID=device-service
MQTT_USERNAME=admin
MQTT_PASSWORD=admin
```

## 🔗 Endpoints

### Base URL

```
http://localhost:8082/device
```

### IoT Device Management Endpoints

| Method | Endpoint                              | Mô tả                                   |
| ------ | ------------------------------------- | --------------------------------------- |
| GET    | `/api/iot-devices`                    | Lấy danh sách tất cả thiết bị IoT       |
| GET    | `/api/iot-devices/active`             | Lấy danh sách thiết bị IoT đã kích hoạt |
| GET    | `/api/iot-devices/registered`         | Lấy danh sách thiết bị IoT đã đăng ký   |
| POST   | `/api/iot-devices/activate`           | Kích hoạt thiết bị IoT                  |
| POST   | `/api/iot-devices/qr-scan`            | Kích hoạt bằng QR Code                  |
| GET    | `/api/iot-devices/{id}/data/latest`   | Lấy dữ liệu sensor mới nhất             |
| GET    | `/api/iot-devices/{id}/data/history`  | Lấy lịch sử dữ liệu sensor              |
| POST   | `/api/iot-devices/{id}/deactivate`    | Tắt thiết bị IoT                        |
| POST   | `/api/iot-devices/{id}/restart`       | Khởi động lại thiết bị IoT              |
| GET    | `/api/iot-devices/dashboard/overview` | Lấy tổng quan dashboard                 |

### Regular Device Management Endpoints

| Method | Endpoint           | Mô tả                         |
| ------ | ------------------ | ----------------------------- |
| GET    | `/devices`         | Lấy danh sách tất cả thiết bị |
| GET    | `/devices/regular` | Lấy danh sách thiết bị thường |
| GET    | `/devices/iot`     | Lấy danh sách thiết bị IoT    |
| POST   | `/devices`         | Tạo thiết bị mới              |
| GET    | `/devices/{id}`    | Lấy chi tiết thiết bị         |
| PUT    | `/devices/{id}`    | Cập nhật thiết bị             |
| DELETE | `/devices/{id}`    | Xóa thiết bị                  |

### Borrow/Return Management Endpoints

| Method | Endpoint                            | Mô tả                          |
| ------ | ----------------------------------- | ------------------------------ |
| GET    | `/borrow-records`                   | Lấy danh sách lịch sử mượn/trả |
| POST   | `/borrow-records/borrow`            | Mượn thiết bị                  |
| POST   | `/borrow-records/return`            | Trả thiết bị                   |
| GET    | `/borrow-records/user/{userId}`     | Lấy lịch sử mượn của user      |
| GET    | `/borrow-records/device/{deviceId}` | Lấy lịch sử mượn của thiết bị  |

### Device Category Management Endpoints

| Method | Endpoint           | Mô tả                           |
| ------ | ------------------ | ------------------------------- |
| GET    | `/categories`      | Lấy danh sách danh mục thiết bị |
| POST   | `/categories`      | Tạo danh mục mới                |
| PUT    | `/categories/{id}` | Cập nhật danh mục               |
| DELETE | `/categories/{id}` | Xóa danh mục                    |

## 📊 Health Check

```bash
# Kiểm tra trạng thái service
curl http://localhost:8082/device/actuator/health

# Xem chi tiết health check
curl http://localhost:8082/device/actuator/health -H "Accept: application/json"
```

## 📚 API Documentation

Truy cập Swagger UI để xem và test API:

```
http://localhost:8082/device/swagger-ui/index.html
```

## 🏗️ Cấu trúc dự án

```
device-service/
├── src/main/java/com/ptit/service/
│   ├── DeviceServiceApplication.java     # Main application
│   ├── config/                          # Configuration classes
│   │   ├── AppConfig.java               # Application configuration
│   │   ├── CorsConfig.java              # CORS configuration
│   │   ├── JacksonConfig.java           # JSON configuration
│   │   ├── RabbitMQConfig.java          # RabbitMQ configuration
│   │   └── ...
│   ├── controller/                      # REST controllers
│   │   ├── DeviceController.java        # Device management endpoints
│   │   ├── IotDeviceController.java     # IoT device endpoints
│   │   ├── BorrowRecordController.java  # Borrow/return endpoints
│   │   ├── CategoryController.java      # Category endpoints
│   │   └── ...
│   ├── service/                         # Business logic
│   │   ├── DeviceService.java           # Device management service
│   │   ├── IotDeviceService.java        # IoT device service
│   │   ├── BorrowRecordService.java     # Borrow/return service
│   │   ├── MqttService.java             # MQTT integration service
│   │   └── ...
│   ├── entity/                          # JPA entities
│   │   ├── Device.java                  # Device entity
│   │   ├── IotDevice.java               # IoT device entity
│   │   ├── BorrowRecord.java            # Borrow record entity
│   │   ├── DeviceCategory.java          # Category entity
│   │   └── ...
│   ├── repository/                      # Data access layer
│   │   ├── DeviceRepository.java        # Device repository
│   │   ├── IotDeviceRepository.java     # IoT device repository
│   │   ├── BorrowRecordRepository.java  # Borrow record repository
│   │   └── ...
│   ├── dto/                            # Data transfer objects
│   │   ├── DeviceDTO.java               # Device DTO
│   │   ├── IotDeviceDTO.java            # IoT device DTO
│   │   ├── BorrowRecordDTO.java         # Borrow record DTO
│   │   └── ...
│   └── response/                       # Response objects
│       ├── DataResponse.java            # Standard response
│       ├── DeviceResponse.java          # Device response
│       └── ...
└── src/main/resources/
    └── application.yml                  # Application configuration
```

## 🔧 Cấu hình đặc biệt

### Database Configuration

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
```

### File Upload Configuration

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 20MB
      enabled: true
      file-size-threshold: 2KB
```

### RabbitMQ Configuration

RabbitMQ đã được tắt để tránh lỗi kết nối:

```yaml
rabbitmq:
  enabled: false
```

### MQTT Configuration

```yaml
mqtt:
  broker:
    url: ${MQTT_BROKER_URL}
  client:
    id: ${MQTT_CLIENT_ID}
  username: ${MQTT_USERNAME}
  password: ${MQTT_PASSWORD}
  topic:
    device-register: iot/devices/register
    device-register-response: iot/devices/register/response
    device-data: iot/devices/+/data
    device-status: iot/devices/+/status
    device-commands: iot/devices/+/commands
```

### Health Check Configuration

```yaml
management:
  health:
    rabbit:
      enabled: false
    db:
      enabled: true
```

## 📊 Tính năng chính

### IoT Device Management

- **Auto-registration** thiết bị ESP32 qua MQTT
- **QR Code activation** để kích hoạt thiết bị
- **Real-time data collection** từ sensors
- **Remote control** và monitoring
- **Dashboard overview** cho thiết bị active

### Regular Device Management

- **CRUD operations** cho thiết bị thường
- **Category management** cho phân loại thiết bị
- **Status tracking** và maintenance
- **File attachment** cho tài liệu thiết bị

### Borrow/Return System

- **Borrow/return tracking** cho thiết bị
- **User history** và device history
- **Auto-return scheduling** và notifications
- **Status management** (AVAILABLE, BORROWED, MAINTENANCE)

## 🔒 Bảo mật

### Authentication & Authorization

- **JWT Token** validation
- **Role-based access** control
- **API rate limiting**
- **Input validation**

### IoT Security

- **MQTT Authentication** với username/password
- **Active Code validation** cho device activation
- **Data encryption** cho sensitive data
- **Network isolation** cho IoT devices

## 📈 Performance

### Caching

- **Device data** caching
- **IoT device status** caching
- **Dashboard data** caching

### Optimization

- **Lazy loading** cho relationships
- **Pagination** cho danh sách lớn
- **Async processing** cho MQTT messages

## 🐛 Troubleshooting

### Lỗi kết nối Database

- Kiểm tra PostgreSQL đã chạy chưa
- Kiểm tra thông tin kết nối trong `.env`
- Kiểm tra database `iotlab_db` đã tồn tại chưa

### Lỗi MQTT Connection

- Kiểm tra MQTT broker (Mosquitto) đã chạy chưa
- Kiểm tra credentials trong `.env`
- Kiểm tra network connectivity
- Kiểm tra MQTT topics đã được subscribe chưa

### Lỗi IoT Device Registration

- Kiểm tra Active Code format đúng không
- Kiểm tra MQTT message format
- Kiểm tra device type và capabilities
- Kiểm tra firmware version compatibility

### Lỗi RabbitMQ

- RabbitMQ đã được tắt trong cấu hình
- Nếu cần RabbitMQ, hãy cài đặt và khởi động trước

## 📊 Monitoring

### Metrics

- **Device registration rate:** Tỷ lệ đăng ký thiết bị
- **IoT device activation rate:** Tỷ lệ kích hoạt thiết bị IoT
- **Borrow/return success rate:** Tỷ lệ mượn/trả thành công
- **MQTT message success rate:** Tỷ lệ gửi MQTT thành công

### Logs

- **Access logs:** Log truy cập API
- **MQTT logs:** Log MQTT messages
- **Device logs:** Log hoạt động thiết bị
- **Error logs:** Log lỗi hệ thống

## 🚀 Deployment

### Production Configuration

```yaml
spring:
  jpa:
    show-sql: false
  datasource:
    hikari:
      maximum-pool-size: 20
      connection-timeout: 60000

mqtt:
  broker:
    url: mqtt://your-mqtt-broker:1883
  client:
    id: device-service-prod
```

### Docker Deployment

```bash
# Build image
docker build -t device-service .

# Run container
docker run -d \
  -p 8082:8082 \
  --name device-service \
  device-service
```

## 📞 Liên hệ

Nếu có vấn đề, vui lòng tạo issue trong repository chính.

---

Made with ❤️ by Hung Tran

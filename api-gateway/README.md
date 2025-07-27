# 🌐 API Gateway

> Microservice gateway để điều hướng và quản lý các request cho IoT Lab WebApp

## 📋 Mô tả

API Gateway là một microservice chịu trách nhiệm:

- **Điều hướng request** đến các service phù hợp
- **Authentication & Authorization** cho tất cả API calls
- **Rate limiting** và **Load balancing**
- **CORS configuration** cho frontend
- **API documentation** tổng hợp
- **Request/Response logging** và monitoring
- **Circuit breaker** pattern implementation

## 🚀 Cách chạy

### Yêu cầu hệ thống

- Java 17+
- Maven 3.6+

### Khởi động service

```bash
# Clone repository (nếu chưa có)
cd api-gateway

# Build project
mvn clean install

# Chạy service
mvn spring-boot:run
```

### Cấu hình môi trường

Tạo file `.env` trong thư mục gốc với các biến:

```env
# Eureka Configuration
EUREKA_URI=http://localhost:8761/eureka/

# Gateway Configuration
VALID_TOKEN_URL=http://localhost:8081/user/auth/validate-token

# Service URLs
USER_URL=http://localhost:8081/user
DEVICE_URL=http://localhost:8082/device
PRACTICE_URL=http://localhost:8083/practice
STORAGE_URL=http://localhost:8084/storage
```

## 🔗 Endpoints

### Base URL

```
http://localhost:8080
```

### Gateway Routes

| Service          | Route          | Target URL                          | Mô tả                          |
| ---------------- | -------------- | ----------------------------------- | ------------------------------ |
| User Service     | `/user/**`     | `http://localhost:8081/user/**`     | Quản lý người dùng và xác thực |
| Device Service   | `/device/**`   | `http://localhost:8082/device/**`   | Quản lý thiết bị IoT           |
| Practice Service | `/practice/**` | `http://localhost:8083/practice/**` | Quản lý bài thực hành          |
| Storage Service  | `/storage/**`  | `http://localhost:8084/storage/**`  | Quản lý file upload            |

### Gateway Management Endpoints

| Method | Endpoint           | Mô tả               |
| ------ | ------------------ | ------------------- |
| GET    | `/`                | Gateway dashboard   |
| GET    | `/actuator/health` | Health check        |
| GET    | `/actuator/info`   | Service information |
| GET    | `/api-docs`        | API documentation   |

### Service Discovery Endpoints

| Method | Endpoint                  | Mô tả                      |
| ------ | ------------------------- | -------------------------- |
| GET    | `/services`               | Danh sách tất cả services  |
| GET    | `/services/{serviceName}` | Thông tin chi tiết service |

## 📊 Health Check

```bash
# Kiểm tra trạng thái service
curl http://localhost:8080/actuator/health

# Xem chi tiết health check
curl http://localhost:8080/actuator/health -H "Accept: application/json"
```

## 📚 API Documentation

Truy cập Gateway Dashboard để xem tổng quan tất cả services:

```
http://localhost:8080
```

### Swagger UI Links

| Service          | Swagger UI URL                                       |
| ---------------- | ---------------------------------------------------- |
| User Service     | http://localhost:8080/user/swagger-ui/index.html     |
| Device Service   | http://localhost:8080/device/swagger-ui/index.html   |
| Practice Service | http://localhost:8080/practice/swagger-ui/index.html |
| Storage Service  | http://localhost:8080/storage/swagger-ui/index.html  |

## 🏗️ Cấu trúc dự án

```
api-gateway/
├── src/main/java/com/ptit/service/
│   ├── ApiGatewayApplication.java        # Main application
│   ├── config/                          # Configuration classes
│   │   ├── CorsConfig.java              # CORS configuration
│   │   ├── SecurityConfig.java          # Security configuration
│   │   ├── SpringCloudConfig.java       # Spring Cloud configuration
│   │   └── ...
│   ├── controller/                      # REST controllers
│   │   ├── ApiDocsController.java       # API documentation endpoints
│   │   └── ...
│   ├── service/                         # Business logic
│   │   ├── ApiDocsService.java          # API documentation service
│   │   └── ...
│   ├── dto/                            # Data transfer objects
│   │   ├── ServiceInfo.java             # Service information DTO
│   │   └── ...
│   └── util/                           # Utility classes
└── src/main/resources/
    ├── application.yml                  # Application configuration
    └── templates/
        └── index.html                   # Gateway dashboard template
```

## 🔧 Cấu hình đặc biệt

### Gateway Configuration

```yaml
gateway:
  valid-token-url: ${VALID_TOKEN_URL}
```

### Eureka Client Configuration

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_URI}
```

### CORS Configuration

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        add-to-simple-url-handler-mapping: true
```

### Actuator Configuration

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
```

## 🔒 Bảo mật

### Authentication & Authorization

- **JWT Token validation** cho tất cả protected routes
- **Role-based access control** (RBAC)
- **API key validation** cho external services
- **Request signing** cho sensitive operations

### Security Headers

- **CORS** configuration cho cross-origin requests
- **XSS Protection** headers
- **Content Security Policy** (CSP)
- **HSTS** headers cho HTTPS

### Rate Limiting

- **Request rate limiting** per user/IP
- **Burst protection** cho DDoS attacks
- **Service-specific limits** cho different endpoints

## 📈 Performance

### Load Balancing

- **Round-robin** load balancing
- **Weighted load balancing** cho services
- **Health check-based** routing
- **Circuit breaker** pattern

### Caching

- **Response caching** cho static content
- **Token caching** cho authentication
- **Service discovery** caching

### Optimization

- **Connection pooling** cho downstream services
- **Request compression** (gzip)
- **Response compression** cho large payloads

## 🔄 Routing Rules

### User Service Routes

```
/user/** -> User Service
/auth/** -> User Service (Authentication)
/admin/users/** -> User Service (Admin functions)
```

### Device Service Routes

```
/device/** -> Device Service
/api/iot-devices/** -> Device Service (IoT devices)
/devices/** -> Device Service (Regular devices)
```

### Practice Service Routes

```
/practice/** -> Practice Service
/exams/** -> Practice Service (Exam management)
/questions/** -> Practice Service (Question management)
```

### Storage Service Routes

```
/storage/** -> Storage Service
/files/** -> Storage Service (File management)
/upload/** -> Storage Service (File upload)
```

## 🐛 Troubleshooting

### Lỗi kết nối Eureka

- Kiểm tra Eureka Service đã chạy chưa
- Kiểm tra `EUREKA_URI` trong `.env`
- Kiểm tra network connectivity

### Lỗi Routing

- Kiểm tra service đã đăng ký với Eureka chưa
- Kiểm tra service URL có đúng không
- Kiểm tra service health status

### Lỗi Authentication

- Kiểm tra `VALID_TOKEN_URL` có đúng không
- Kiểm tra User Service đã chạy chưa
- Kiểm tra JWT token format

### Lỗi CORS

- Kiểm tra CORS configuration
- Kiểm tra frontend origin có được allow không
- Kiểm tra preflight requests

## 📊 Monitoring

### Metrics

- **Request count** per service
- **Response time** per route
- **Error rate** per endpoint
- **Circuit breaker status** per service

### Logs

- **Access logs:** Log tất cả requests
- **Error logs:** Log routing errors
- **Security logs:** Log authentication failures
- **Performance logs:** Log slow requests

### Alerts

- **Service down** alerts
- **High error rate** alerts
- **Circuit breaker** alerts
- **Authentication failure** alerts

## 🚀 Deployment

### Production Configuration

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user/**
        - id: device-service
          uri: lb://DEVICE-SERVICE
          predicates:
            - Path=/device/**
```

### Docker Deployment

```bash
# Build image
docker build -t api-gateway .

# Run container
docker run -d \
  -p 8080:8080 \
  --name api-gateway \
  api-gateway
```

### Load Balancer Configuration

```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
      health-check:
        initial-delay: 0
        interval: 30s
```

## 📞 Liên hệ

Nếu có vấn đề, vui lòng tạo issue trong repository chính.

---

Made with ❤️ by Hung Tran

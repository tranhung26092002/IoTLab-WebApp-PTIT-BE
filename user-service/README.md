# 👤 User Service

> Microservice quản lý người dùng và xác thực cho IoT Lab WebApp

## 📋 Mô tả

User Service là một microservice chịu trách nhiệm quản lý:

- **Đăng ký và đăng nhập** người dùng
- **Xác thực JWT** và quản lý token
- **OAuth2** (Google, Facebook)
- **Quản lý thông tin cá nhân** người dùng
- **Gửi email** thông báo và OTP
- **Quản lý địa chỉ** người dùng

## 🚀 Cách chạy

### Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- PostgreSQL 15 (tùy chọn)

### Khởi động service

```bash
# Clone repository (nếu chưa có)
cd user-service

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

# JWT Configuration
JWT_SECRET=your-secret-key-here-make-it-long-and-secure
REFRESH_TOKEN_KEY=your-refresh-token-key-here

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
FACEBOOK_CLIENT_ID=your-facebook-client-id
FACEBOOK_CLIENT_SECRET=your-facebook-client-secret

# Email Configuration (Brevo)
BREVO_API_KEY=your-brevo-api-key
BREVO_SENDER_EMAIL=noreply@yourdomain.com
BREVO_SENDER_NAME=IoTLab System

# Environment
ENVIRONMENT=development
DEBUG=false
ASYNC_CORE_POOL_SIZE=5

# External Services
STORAGE_SERVICE=http://localhost:8084/storage
```

## 🔗 Endpoints

### Base URL

```
http://localhost:8081/user
```

### Authentication Endpoints

| Method | Endpoint                | Mô tả                 |
| ------ | ----------------------- | --------------------- |
| POST   | `/auth/register`        | Đăng ký tài khoản mới |
| POST   | `/auth/login`           | Đăng nhập             |
| POST   | `/auth/refresh-token`   | Làm mới token         |
| POST   | `/auth/logout`          | Đăng xuất             |
| POST   | `/auth/forgot-password` | Quên mật khẩu         |
| POST   | `/auth/reset-password`  | Đặt lại mật khẩu      |
| POST   | `/auth/verify-otp`      | Xác thực OTP          |

### OAuth2 Endpoints

| Method | Endpoint                              | Mô tả              |
| ------ | ------------------------------------- | ------------------ |
| GET    | `/auth/oauth2/authorization/google`   | Đăng nhập Google   |
| GET    | `/auth/oauth2/authorization/facebook` | Đăng nhập Facebook |
| GET    | `/auth/oauth2/callback/google`        | Callback Google    |
| GET    | `/auth/oauth2/callback/facebook`      | Callback Facebook  |

### User Management Endpoints

| Method | Endpoint                 | Mô tả                      |
| ------ | ------------------------ | -------------------------- |
| GET    | `/users/profile`         | Lấy thông tin cá nhân      |
| PUT    | `/users/profile`         | Cập nhật thông tin cá nhân |
| PUT    | `/users/change-password` | Đổi mật khẩu               |
| GET    | `/users/addresses`       | Lấy danh sách địa chỉ      |
| POST   | `/users/addresses`       | Thêm địa chỉ mới           |
| PUT    | `/users/addresses/{id}`  | Cập nhật địa chỉ           |
| DELETE | `/users/addresses/{id}`  | Xóa địa chỉ                |

### Admin Endpoints

| Method | Endpoint                   | Mô tả                            |
| ------ | -------------------------- | -------------------------------- |
| GET    | `/admin/users`             | Lấy danh sách tất cả người dùng  |
| GET    | `/admin/users/{id}`        | Lấy thông tin người dùng theo ID |
| PUT    | `/admin/users/{id}/status` | Cập nhật trạng thái người dùng   |
| DELETE | `/admin/users/{id}`        | Xóa người dùng                   |

## 📊 Health Check

```bash
# Kiểm tra trạng thái service
curl http://localhost:8081/user/actuator/health

# Xem chi tiết health check
curl http://localhost:8081/user/actuator/health -H "Accept: application/json"
```

## 📚 API Documentation

Truy cập Swagger UI để xem và test API:

```
http://localhost:8081/user/swagger-ui/index.html
```

## 🏗️ Cấu trúc dự án

```
user-service/
├── src/main/java/com/ptit/service/
│   ├── UserServiceApplication.java      # Main application
│   ├── config/                         # Configuration classes
│   │   ├── SecurityConfig.java         # Security configuration
│   │   ├── RabbitMQConfig.java         # RabbitMQ configuration
│   │   └── ...
│   ├── controller/                     # REST controllers
│   │   ├── AuthController.java         # Authentication endpoints
│   │   ├── UserController.java         # User management endpoints
│   │   └── ...
│   ├── service/                        # Business logic
│   │   ├── AuthService.java            # Authentication service
│   │   ├── UserService.java            # User management service
│   │   └── ...
│   ├── entity/                         # JPA entities
│   │   ├── User.java                   # User entity
│   │   ├── Address.java                # Address entity
│   │   └── ...
│   ├── repository/                     # Data access layer
│   │   ├── UserRepository.java         # User repository
│   │   ├── AddressRepository.java      # Address repository
│   │   └── ...
│   └── dto/                           # Data transfer objects
│       ├── LoginRequest.java           # Login request DTO
│       ├── RegisterRequest.java        # Register request DTO
│       └── ...
└── src/main/resources/
    ├── application.yml                 # Application configuration
    ├── templates/                      # Email templates
    └── i18n/                          # Internationalization
```

## 🔧 Cấu hình đặc biệt

### RabbitMQ Configuration

RabbitMQ đã được tắt để tránh lỗi kết nối:

```yaml
rabbitmq:
  enabled: false
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

### JWT Configuration

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000 # 24 hours
```

## 🐛 Troubleshooting

### Lỗi kết nối Database

- Kiểm tra PostgreSQL đã chạy chưa
- Kiểm tra thông tin kết nối trong `.env`
- Kiểm tra database `iotlab_db` đã tồn tại chưa

### Lỗi OAuth2

- Kiểm tra Client ID và Client Secret đã đúng chưa
- Kiểm tra Redirect URI đã được cấu hình trong Google/Facebook Console

### Lỗi Email

- Kiểm tra Brevo API Key
- Kiểm tra email sender đã được xác thực

## 📞 Liên hệ

Nếu có vấn đề, vui lòng tạo issue trong repository chính.

---

Made with ❤️ by Hung Tran

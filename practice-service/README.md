# 📚 Practice Service

> Microservice quản lý bài thực hành và bài thi cho IoT Lab WebApp

## 📋 Mô tả

Practice Service là một microservice chịu trách nhiệm quản lý:

- **Bài thực hành** và hướng dẫn
- **Bài thi** và câu hỏi
- **Quản lý file** tài liệu và video
- **Theo dõi tiến độ** học tập
- **Đánh giá** và chấm điểm
- **Thống kê** kết quả học tập

## 🚀 Cách chạy

### Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- PostgreSQL 15 (tùy chọn)

### Khởi động service

```bash
# Clone repository (nếu chưa có)
cd practice-service

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
```

## 🔗 Endpoints

### Base URL

```
http://localhost:8083/practice
```

### Practice Management Endpoints

| Method | Endpoint                | Mô tả                         |
| ------ | ----------------------- | ----------------------------- |
| GET    | `/practices`            | Lấy danh sách bài thực hành   |
| POST   | `/practices`            | Tạo bài thực hành mới         |
| GET    | `/practices/{id}`       | Lấy chi tiết bài thực hành    |
| PUT    | `/practices/{id}`       | Cập nhật bài thực hành        |
| DELETE | `/practices/{id}`       | Xóa bài thực hành             |
| GET    | `/practices/{id}/files` | Lấy file của bài thực hành    |
| POST   | `/practices/{id}/files` | Upload file cho bài thực hành |

### Exam Management Endpoints

| Method | Endpoint                | Mô tả                    |
| ------ | ----------------------- | ------------------------ |
| GET    | `/exams`                | Lấy danh sách bài thi    |
| POST   | `/exams`                | Tạo bài thi mới          |
| GET    | `/exams/{id}`           | Lấy chi tiết bài thi     |
| PUT    | `/exams/{id}`           | Cập nhật bài thi         |
| DELETE | `/exams/{id}`           | Xóa bài thi              |
| GET    | `/exams/{id}/questions` | Lấy câu hỏi của bài thi  |
| POST   | `/exams/{id}/questions` | Thêm câu hỏi vào bài thi |

### Question Management Endpoints

| Method | Endpoint          | Mô tả                 |
| ------ | ----------------- | --------------------- |
| GET    | `/questions`      | Lấy danh sách câu hỏi |
| POST   | `/questions`      | Tạo câu hỏi mới       |
| GET    | `/questions/{id}` | Lấy chi tiết câu hỏi  |
| PUT    | `/questions/{id}` | Cập nhật câu hỏi      |
| DELETE | `/questions/{id}` | Xóa câu hỏi           |

### Progress Tracking Endpoints

| Method | Endpoint                  | Mô tả                        |
| ------ | ------------------------- | ---------------------------- |
| GET    | `/progress/user/{userId}` | Lấy tiến độ học tập của user |
| POST   | `/progress/update`        | Cập nhật tiến độ học tập     |
| GET    | `/progress/statistics`    | Lấy thống kê tiến độ         |

### File Management Endpoints

| Method | Endpoint        | Mô tả                |
| ------ | --------------- | -------------------- |
| POST   | `/files/upload` | Upload file tài liệu |
| GET    | `/files/{id}`   | Download file        |
| DELETE | `/files/{id}`   | Xóa file             |
| GET    | `/files`        | Lấy danh sách file   |

## 📊 Health Check

```bash
# Kiểm tra trạng thái service
curl http://localhost:8083/practice/actuator/health

# Xem chi tiết health check
curl http://localhost:8083/practice/actuator/health -H "Accept: application/json"
```

## 📚 API Documentation

Truy cập Swagger UI để xem và test API:

```
http://localhost:8083/practice/swagger-ui/index.html
```

## 🏗️ Cấu trúc dự án

```
practice-service/
├── src/main/java/com/ptit/service/
│   ├── PracticeServiceApplication.java   # Main application
│   ├── config/                          # Configuration classes
│   │   ├── CorsConfig.java              # CORS configuration
│   │   ├── JacksonConfig.java           # JSON configuration
│   │   ├── ModelMapperConfig.java       # Object mapping
│   │   └── ...
│   ├── controller/                      # REST controllers
│   │   ├── PracticeController.java      # Practice endpoints
│   │   ├── ExamController.java          # Exam endpoints
│   │   ├── QuestionController.java      # Question endpoints
│   │   └── ...
│   ├── service/                         # Business logic
│   │   ├── PracticeService.java         # Practice service
│   │   ├── ExamService.java             # Exam service
│   │   ├── QuestionService.java         # Question service
│   │   ├── FileService.java             # File service
│   │   └── ...
│   ├── entity/                          # JPA entities
│   │   ├── Practice.java                # Practice entity
│   │   ├── Exam.java                    # Exam entity
│   │   ├── Question.java                # Question entity
│   │   ├── PracticeFile.java            # Practice file entity
│   │   └── ...
│   ├── repository/                      # Data access layer
│   │   ├── PracticeRepository.java      # Practice repository
│   │   ├── ExamRepository.java          # Exam repository
│   │   ├── QuestionRepository.java      # Question repository
│   │   └── ...
│   ├── dto/                            # Data transfer objects
│   │   ├── PracticeDTO.java             # Practice DTO
│   │   ├── ExamDTO.java                 # Exam DTO
│   │   ├── QuestionDTO.java             # Question DTO
│   │   └── ...
│   └── response/                       # Response objects
│       ├── DataResponse.java            # Standard response
│       ├── MessageResponse.java         # Message response
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
      max-file-size: 200MB
      max-request-size: 200MB
      enabled: true
      file-size-threshold: 10KB
```

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

## 📊 Tính năng chính

### Practice Management

- **Tạo và quản lý** bài thực hành
- **Upload tài liệu** và video hướng dẫn
- **Theo dõi tiến độ** của học viên
- **Đánh giá** kết quả thực hành

### Exam Management

- **Tạo bài thi** với nhiều loại câu hỏi
- **Quản lý câu hỏi** (trắc nghiệm, tự luận)
- **Tính điểm** tự động
- **Thống kê** kết quả thi

### File Management

- **Upload/Download** file tài liệu
- **Hỗ trợ nhiều định dạng** (PDF, DOC, MP4, etc.)
- **Tích hợp** với Storage Service
- **Quản lý metadata** file

## 🔒 Bảo mật

### Authentication & Authorization

- **JWT Token** validation
- **Role-based access** control
- **API rate limiting**
- **Input validation**

### Data Protection

- **Encryption** cho dữ liệu nhạy cảm
- **Audit logging** cho thay đổi
- **Backup** dữ liệu định kỳ

## 📈 Performance

### Caching

- **Practice data** caching
- **Exam questions** caching
- **File metadata** caching

### Optimization

- **Lazy loading** cho relationships
- **Pagination** cho danh sách lớn
- **Async processing** cho file upload

## 🐛 Troubleshooting

### Lỗi kết nối Database

- Kiểm tra PostgreSQL đã chạy chưa
- Kiểm tra thông tin kết nối trong `.env`
- Kiểm tra database `iotlab_db` đã tồn tại chưa

### Lỗi Upload File

- Kiểm tra kích thước file có vượt quá giới hạn không
- Kiểm tra định dạng file có được hỗ trợ không
- Kiểm tra kết nối đến Storage Service

### Lỗi RabbitMQ

- RabbitMQ đã được tắt trong cấu hình
- Nếu cần RabbitMQ, hãy cài đặt và khởi động trước

## 📊 Monitoring

### Metrics

- **Practice completion rate:** Tỷ lệ hoàn thành bài thực hành
- **Exam success rate:** Tỷ lệ đỗ bài thi
- **File upload success:** Tỷ lệ upload file thành công
- **Response time:** Thời gian phản hồi API

### Logs

- **Access logs:** Log truy cập API
- **Error logs:** Log lỗi hệ thống
- **Business logs:** Log hoạt động nghiệp vụ

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
```

### Docker Deployment

```bash
# Build image
docker build -t practice-service .

# Run container
docker run -d \
  -p 8083:8083 \
  --name practice-service \
  practice-service
```

## 📞 Liên hệ

Nếu có vấn đề, vui lòng tạo issue trong repository chính.

---

Made with ❤️ by Hung Tran

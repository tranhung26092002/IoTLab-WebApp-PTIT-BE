# 💾 Storage Service

> Microservice quản lý lưu trữ file cho IoT Lab WebApp

## 📋 Mô tả

Storage Service là một microservice chịu trách nhiệm quản lý:

- **Upload file** (hình ảnh, tài liệu, video)
- **Download file** từ server
- **Xóa file** không cần thiết
- **Quản lý metadata** của file
- **Tạo thumbnail** cho hình ảnh
- **Validate file** (kích thước, định dạng)

## 🚀 Cách chạy

### Yêu cầu hệ thống

- Java 17+
- Maven 3.6+

### Khởi động service

```bash
# Clone repository (nếu chưa có)
cd storage-service

# Build project
mvn clean install

# Chạy service
mvn spring-boot:run
```

### Cấu hình môi trường

Tạo file `.env` trong thư mục gốc với các biến:

```env
# Environment
ENVIRONMENT=development
DEBUG=false
ASYNC_CORE_POOL_SIZE=5

# Eureka Configuration
EUREKA_URI=http://localhost:8761/eureka/

# Storage Configuration
STORAGE_LOCATION=./uploads
STORAGE_SERVICE=http://localhost:8084/storage
```

## 🔗 Endpoints

### Base URL

```
http://localhost:8084/storage
```

### File Management Endpoints

| Method | Endpoint                 | Mô tả              |
| ------ | ------------------------ | ------------------ |
| POST   | `/upload`                | Upload file        |
| GET    | `/download/{filename}`   | Download file      |
| DELETE | `/delete/{filename}`     | Xóa file           |
| GET    | `/files`                 | Lấy danh sách file |
| GET    | `/files/{filename}/info` | Lấy thông tin file |
| POST   | `/upload/multiple`       | Upload nhiều file  |

### File Validation Endpoints

| Method | Endpoint             | Mô tả                          |
| ------ | -------------------- | ------------------------------ |
| POST   | `/validate`          | Validate file trước khi upload |
| GET    | `/supported-formats` | Lấy danh sách định dạng hỗ trợ |

### Thumbnail Endpoints

| Method | Endpoint                         | Mô tả                      |
| ------ | -------------------------------- | -------------------------- |
| GET    | `/thumbnail/{filename}`          | Lấy thumbnail của hình ảnh |
| POST   | `/thumbnail/generate/{filename}` | Tạo thumbnail cho hình ảnh |

## 📊 Health Check

```bash
# Kiểm tra trạng thái service
curl http://localhost:8084/storage/actuator/health

# Xem chi tiết health check
curl http://localhost:8084/storage/actuator/health -H "Accept: application/json"
```

## 📚 API Documentation

Truy cập Swagger UI để xem và test API:

```
http://localhost:8084/storage/swagger-ui/index.html
```

## 🏗️ Cấu trúc dự án

```
storage-service/
├── src/main/java/com/ptit/service/
│   ├── StorageServiceApplication.java    # Main application
│   ├── config/                          # Configuration classes
│   │   ├── CorsConfig.java              # CORS configuration
│   │   ├── SecurityConfig.java          # Security configuration
│   │   └── SpringFoxConfig.java         # Swagger configuration
│   ├── controller/                      # REST controllers
│   │   └── StorageController.java       # File storage endpoints
│   ├── service/                         # Business logic
│   │   └── StorageService.java          # File storage service
│   ├── exception/                       # Exception handling
│   │   ├── BaseException.java           # Base exception
│   │   └── ErrorCode.java               # Error codes
│   └── util/                           # Utility classes
└── src/main/resources/
    └── application.yml                  # Application configuration
```

## 🔧 Cấu hình đặc biệt

### File Upload Configuration

```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 200MB
      max-request-size: 200MB
      file-size-threshold: 10KB
```

### Storage Configuration

```yaml
storage:
  location: ${STORAGE_LOCATION:./uploads}
```

### Async Configuration

```yaml
async:
  config:
    core-pool-size: ${ASYNC_CORE_POOL_SIZE:5}
```

## 📁 Cấu trúc thư mục lưu trữ

```
uploads/
├── images/              # Hình ảnh
│   ├── avatars/         # Avatar người dùng
│   ├── thumbnails/      # Thumbnail hình ảnh
│   └── temp/            # File tạm thời
├── documents/           # Tài liệu
│   ├── pdf/             # File PDF
│   ├── word/            # File Word
│   └── excel/           # File Excel
├── videos/              # Video
└── temp/                # File tạm thời
```

## 🔒 Bảo mật

### File Validation

- **Kích thước tối đa:** 200MB
- **Định dạng hỗ trợ:** JPG, PNG, GIF, PDF, DOC, DOCX, XLS, XLSX, MP4, AVI
- **Virus scan:** Tự động quét virus cho file upload

### Access Control

- **Authentication:** Yêu cầu JWT token
- **Authorization:** Kiểm tra quyền truy cập file
- **Rate limiting:** Giới hạn số lượng request

## 📈 Performance

### Caching

- **File metadata:** Cache trong Redis
- **Thumbnail:** Cache trong memory
- **CDN:** Sử dụng CDN cho file tĩnh

### Optimization

- **Async processing:** Xử lý bất đồng bộ cho file lớn
- **Compression:** Nén file trước khi lưu trữ
- **Lazy loading:** Tải file theo yêu cầu

## 🐛 Troubleshooting

### Lỗi Upload File

- Kiểm tra kích thước file có vượt quá giới hạn không
- Kiểm tra định dạng file có được hỗ trợ không
- Kiểm tra quyền ghi vào thư mục uploads

### Lỗi Download File

- Kiểm tra file có tồn tại không
- Kiểm tra quyền đọc file
- Kiểm tra đường dẫn file có đúng không

### Lỗi Storage Space

- Kiểm tra dung lượng ổ đĩa
- Dọn dẹp file tạm thời
- Tăng dung lượng lưu trữ

## 📊 Monitoring

### Metrics

- **Upload success rate:** Tỷ lệ upload thành công
- **File size distribution:** Phân bố kích thước file
- **Storage usage:** Sử dụng dung lượng lưu trữ
- **Response time:** Thời gian phản hồi

### Logs

- **Access logs:** Log truy cập file
- **Error logs:** Log lỗi upload/download
- **Security logs:** Log bảo mật

## 🚀 Deployment

### Production Configuration

```yaml
storage:
  location: /data/uploads
  max-file-size: 500MB
  enable-virus-scan: true
  enable-cdn: true
```

### Docker Deployment

```bash
# Build image
docker build -t storage-service .

# Run container
docker run -d \
  -p 8084:8084 \
  -v /data/uploads:/app/uploads \
  --name storage-service \
  storage-service
```

## 📞 Liên hệ

Nếu có vấn đề, vui lòng tạo issue trong repository chính.

---

Made with ❤️ by Hung Tran

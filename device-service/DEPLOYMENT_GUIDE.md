# Hướng dẫn triển khai hệ thống IoT Device Management

## 🚀 Tổng quan triển khai

Hệ thống IoT Device Management đã được thiết kế hoàn chỉnh với các thành phần:
- **Backend Spring Boot** - API và xử lý dữ liệu
- **MQTT Broker** - Giao tiếp với ESP32
- **WebSocket** - Realtime data transmission
- **Database PostgreSQL** - Lưu trữ dữ liệu
- **ESP32 Firmware** - Thiết bị IoT

## 📋 Yêu cầu hệ thống

### Backend Requirements
- Java 8+
- Maven 3.6+
- PostgreSQL 12+
- MQTT Broker (Mosquitto)
- 4GB RAM minimum
- 10GB disk space

### ESP32 Requirements
- ESP32 Development Board
- DHT22 Sensor (Temperature & Humidity)
- BH1750 Sensor (Light)
- Micro USB Cable
- Power Supply

## 🔧 Cài đặt và cấu hình

### 1. Cài đặt MQTT Broker (Mosquitto)

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mosquitto mosquitto-clients

# Windows
# Download từ https://mosquitto.org/download/

# macOS
brew install mosquitto
```

**Cấu hình Mosquitto:**
```bash
# Tạo file cấu hình
sudo nano /etc/mosquitto/mosquitto.conf

# Thêm các dòng sau:
listener 1883
allow_anonymous true
persistence true
persistence_location /var/lib/mosquitto/
log_dest file /var/log/mosquitto/mosquitto.log
```

**Khởi động Mosquitto:**
```bash
sudo systemctl enable mosquitto
sudo systemctl start mosquitto
```

### 2. Cài đặt PostgreSQL

```bash
# Ubuntu/Debian
sudo apt install postgresql postgresql-contrib

# Tạo database
sudo -u postgres createdb iot_device_management
sudo -u postgres createuser iot_user
sudo -u postgres psql -c "ALTER USER iot_user WITH PASSWORD 'iot_password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE iot_device_management TO iot_user;"
```

### 3. Cấu hình Backend

**Tạo file `application.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/iot_device_management
    username: iot_user
    password: iot_password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

mqtt:
  broker: tcp://localhost:1883
  client-id: iot-backend
  username: 
  password: 
  topics:
    - iot/devices/register
    - iot/devices/+/data
    - iot/devices/+/status

server:
  port: 8080

logging:
  level:
    com.ptit.service: DEBUG
    org.springframework.integration.mqtt: DEBUG
```

### 4. Build và chạy Backend

```bash
# Clone repository
git clone <repository-url>
cd device-service

# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

### 5. Cài đặt ESP32 Firmware

**Yêu cầu:**
- PlatformIO IDE
- ESP32 Board Support

**Cấu hình PlatformIO:**
```ini
[env:esp32dev]
platform = espressif32
board = esp32dev
framework = arduino
monitor_speed = 115200
lib_deps = 
    adafruit/DHT sensor library@^1.4.4
    adafruit/Adafruit Unified Sensor@^1.1.9
    knolleary/PubSubClient@^2.8
    bblanchon/ArduinoJson@^6.21.3
```

**Upload firmware:**
```bash
pio run --target upload
pio device monitor
```

## 🔄 Luồng hoạt động

### 1. Khởi tạo thiết bị ESP32
1. ESP32 khởi động ở Access Point mode
2. Admin kết nối WiFi "ESP32_Device_[MAC]"
3. Truy cập http://192.168.4.1
4. Cấu hình WiFi lab và tên thiết bị
5. ESP32 chuyển sang Station mode

### 2. Auto-Registration
1. ESP32 kết nối MQTT Broker
2. Gửi registration message đến `iot/devices/register`
3. Backend tạo device với status "REGISTERED"
4. Gửi WebSocket notification

### 3. QR Code Activation
1. Admin quét QR code chứa Active Code
2. Backend đổi status thành "ACTIVE"
3. Gửi activation command đến ESP32
4. ESP32 bắt đầu gửi sensor data

### 4. Data Collection
1. ESP32 gửi data đến `iot/devices/{device_id}/data`
2. Backend lưu vào database
3. Gửi realtime data qua WebSocket
4. Frontend hiển thị dashboard

## 🧪 Testing

### 1. Test MQTT Connection

```bash
# Subscribe to registration topic
mosquitto_sub -h localhost -t "iot/devices/register" -v

# Publish test registration
mosquitto_pub -h localhost -t "iot/devices/register" -m '{
  "mac_address": "AA:BB:CC:DD:EE:FF",
  "device_name": "Test Sensor",
  "active_code": "IOT_ACT_123456789",
  "device_type": "TEMPERATURE_HUMIDITY_SENSOR",
  "firmware_version": "1.0.0",
  "sensors": "DHT22,BMP280",
  "capabilities": "temperature,humidity,pressure",
  "wifi_ssid": "PTIT_LAB_WIFI"
}'
```

### 2. Test WebSocket

```javascript
// Kết nối WebSocket
const socket = new WebSocket('ws://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected to WebSocket');
    
    // Subscribe to sensor data
    stompClient.subscribe('/iot/sensor-data', function (message) {
        console.log('Sensor data:', JSON.parse(message.body));
    });
});
```

### 3. Test API Endpoints

```bash
# Get all IoT devices
curl -X GET http://localhost:8080/api/iot-devices

# Get active devices
curl -X GET http://localhost:8080/api/iot-devices/active

# Get latest sensor data
curl -X GET http://localhost:8080/api/iot/realtime/latest-sensor-data

# Activate device
curl -X POST http://localhost:8080/api/iot-devices/qr-scan \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "qrCodeData=IOT_ACT_123456789"
```

## 📊 Monitoring

### 1. Log Files
```bash
# Backend logs
tail -f logs/application.log

# MQTT logs
tail -f /var/log/mosquitto/mosquitto.log

# ESP32 serial monitor
pio device monitor
```

### 2. Database Monitoring
```sql
-- Check device status
SELECT code, name, status, last_seen FROM devices WHERE is_iot_device = true;

-- Check sensor data
SELECT device_id, temperature, humidity, timestamp 
FROM iot_sensor_data 
ORDER BY timestamp DESC 
LIMIT 10;
```

### 3. MQTT Topics Monitoring
```bash
# Monitor all IoT topics
mosquitto_sub -h localhost -t "iot/#" -v
```

## 🔧 Troubleshooting

### Common Issues

1. **ESP32 không kết nối WiFi**
   - Kiểm tra credentials
   - Reset ESP32
   - Kiểm tra signal strength

2. **MQTT Connection Failed**
   - Kiểm tra MQTT broker status
   - Verify network connectivity
   - Check firewall settings

3. **Database Connection Error**
   - Verify PostgreSQL service
   - Check credentials
   - Ensure database exists

4. **WebSocket Connection Failed**
   - Check CORS configuration
   - Verify WebSocket endpoint
   - Check browser console

### Performance Tuning

1. **Database Optimization**
   ```sql
   -- Create indexes
   CREATE INDEX idx_iot_sensor_data_device_timestamp 
   ON iot_sensor_data(device_id, timestamp);
   
   -- Partition sensor data by date
   CREATE TABLE iot_sensor_data_2024 PARTITION OF iot_sensor_data
   FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
   ```

2. **MQTT Optimization**
   ```bash
   # Increase message queue size
   max_queued_messages 1000
   
   # Enable persistence
   persistence true
   persistence_location /var/lib/mosquitto/
   ```

3. **JVM Tuning**
   ```bash
   # Increase heap size
   java -Xmx2g -Xms1g -jar device-service.jar
   ```

## 🚀 Production Deployment

### 1. Docker Deployment

**Dockerfile:**
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/device-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: iot_device_management
      POSTGRES_USER: iot_user
      POSTGRES_PASSWORD: iot_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  mosquitto:
    image: eclipse-mosquitto:2.0
    ports:
      - "1883:1883"
      - "9001:9001"
    volumes:
      - mosquitto_data:/mosquitto/data
      - mosquitto_logs:/mosquitto/log

  device-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/iot_device_management
      MQTT_BROKER: tcp://mosquitto:1883
    depends_on:
      - postgres
      - mosquitto

volumes:
  postgres_data:
  mosquitto_data:
  mosquitto_logs:
```

### 2. Kubernetes Deployment

**deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: device-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: device-service
  template:
    metadata:
      labels:
        app: device-service
    spec:
      containers:
      - name: device-service
        image: device-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
```

## 📈 Scaling

### 1. Horizontal Scaling
- Deploy multiple backend instances
- Use load balancer
- Configure database connection pooling

### 2. Vertical Scaling
- Increase JVM heap size
- Optimize database queries
- Use caching (Redis)

### 3. IoT Device Scaling
- Implement device clustering
- Use MQTT clustering
- Implement data aggregation

## 🔒 Security

### 1. MQTT Security
```bash
# Enable TLS
listener 8883
certfile /etc/mosquitto/certs/server.crt
keyfile /etc/mosquitto/certs/server.key

# Enable authentication
password_file /etc/mosquitto/passwd
```

### 2. API Security
- Implement JWT authentication
- Use HTTPS
- Rate limiting
- Input validation

### 3. Database Security
- Encrypt data at rest
- Regular backups
- Access control
- Audit logging

## 📝 Maintenance

### 1. Regular Tasks
- Database backups
- Log rotation
- Security updates
- Performance monitoring

### 2. Monitoring Alerts
- Device offline alerts
- Database connection issues
- MQTT broker status
- API response times

### 3. Backup Strategy
```bash
# Database backup
pg_dump iot_device_management > backup_$(date +%Y%m%d).sql

# Configuration backup
tar -czf config_backup_$(date +%Y%m%d).tar.gz /etc/mosquitto/
```

---

## 🎯 Kết luận

Hệ thống IoT Device Management đã sẵn sàng triển khai với:
- ✅ Backend API hoàn chỉnh
- ✅ MQTT integration
- ✅ WebSocket realtime
- ✅ Database schema
- ✅ ESP32 firmware design
- ✅ Deployment guide

Bước tiếp theo: Triển khai và test với thiết bị ESP32 thực tế! 🚀 
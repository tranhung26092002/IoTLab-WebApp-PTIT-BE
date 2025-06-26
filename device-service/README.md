# Hệ thống Quản lý Thiết bị IoT WiFi (ESP32)

## Tổng quan

Hệ thống đã được nâng cấp để hỗ trợ quản lý thiết bị IoT WiFi sử dụng ESP32, bao gồm:
- **Cấu hình WiFi qua Access Point Mode** của ESP32
- **Auto-registration** thiết bị IoT với Active Code
- **QR Code activation** để kích hoạt thiết bị
- **Thu thập dữ liệu real-time** từ sensors
- **Dashboard hiển thị** chỉ thiết bị đã active
- **Quản lý trạng thái và điều khiển** thiết bị từ xa

## Cấu trúc Database

### Bảng mới được thêm:

1. **iot_sensor_data** - Lưu trữ dữ liệu từ sensors
2. **iot_device_config** - Cấu hình thiết bị IoT
3. **iot_device_commands** - Lệnh điều khiển thiết bị

### Bảng devices được cập nhật:
- Thêm các trường IoT: `is_iot_device`, `active_code`, `mac_address`, `ip_address`, `firmware_version`, `wifi_ssid`, `last_seen`, `activated_at`, `activated_by`
- Cập nhật enum `DeviceStatus` để hỗ trợ: `REGISTERED`, `ACTIVE`, `OFFLINE`, `ERROR`, `DEACTIVATED`

## API Endpoints

### IoT Device Management

#### Lấy danh sách thiết bị
```http
GET /device/api/iot-devices
GET /device/api/iot-devices/active
GET /device/api/iot-devices/registered
```

#### Kích hoạt thiết bị
```http
POST /device/api/iot-devices/activate
Content-Type: application/json

{
  "activeCode": "IOT_ACT_123456789",
  "deviceName": "Temperature Sensor Lab A",
  "description": "Monitor temperature in Lab A",
  "location": "Lab A - Room 101",
  "dataInterval": 30,
  "alertThresholds": "{\"temperature_min\":18,\"temperature_max\":30}",
  "displayConfig": "{\"show_on_dashboard\":true}"
}
```

#### Kích hoạt bằng QR Code
```http
POST /device/api/iot-devices/qr-scan?qrCodeData=IOT_ACT_123456789
```

#### Dữ liệu sensors
```http
GET /device/api/iot-devices/{deviceId}/data/latest
GET /device/api/iot-devices/{deviceId}/data/history?startTime=2024-01-15T00:00:00&endTime=2024-01-15T23:59:59
```

#### Điều khiển thiết bị
```http
POST /device/api/iot-devices/{deviceId}/deactivate
POST /device/api/iot-devices/{deviceId}/restart
```

#### Dashboard
```http
GET /device/api/iot-devices/dashboard/overview
```

### Regular Device Management (Giữ nguyên)
```http
GET /device/devices/regular
GET /device/devices/iot
```

## MQTT Configuration

### Cấu hình trong application.yml
```yaml
mqtt:
  broker:
    url: ${MQTT_BROKER_URL:localhost:1883}
  client:
    id: ${MQTT_CLIENT_ID:device-service}
  username: ${MQTT_USERNAME:admin}
  password: ${MQTT_PASSWORD:admin}
```

### MQTT Topics

#### Registration
- `iot/devices/register` - ESP32 gửi registration message
- `iot/devices/register/response` - Server trả về registration info

#### Device-specific
- `iot/devices/{device_id}/data` - Device gửi sensor data
- `iot/devices/{device_id}/config` - Server gửi config updates
- `iot/devices/{device_id}/commands` - Server gửi commands
- `iot/devices/{device_id}/status` - Device gửi status updates

## Workflow

### 1. Cấu hình WiFi cho ESP32
1. ESP32 khởi động ở chế độ Access Point
2. Admin kết nối vào WiFi "ESP32_Device_[MAC]"
3. Admin truy cập http://192.168.4.1
4. Nhập thông tin WiFi lab và tên thiết bị
5. ESP32 lưu cấu hình và chuyển sang Station Mode

### 2. Auto-Registration
1. ESP32 kết nối WiFi lab và MQTT Broker
2. ESP32 gửi registration message với Active Code
3. Server tạo device với status "REGISTERED"
4. Server gửi registration response

### 3. QR Code Activation
1. Admin in QR code chứa Active Code
2. Admin quét QR code trên web app
3. Server kích hoạt device (status = "ACTIVE")
4. Device hiển thị trên dashboard

### 4. Thu thập dữ liệu
1. ESP32 gửi sensor data theo interval
2. Server lưu dữ liệu vào database
3. Dashboard hiển thị real-time (chỉ thiết bị ACTIVE)

## ESP32 Firmware Requirements

### Cấu trúc firmware cần có:
- WiFi Management (AP Mode + Station Mode)
- Web Server cho cấu hình
- MQTT Client
- Active Code Management
- Sensor Management
- Command Processing

### Active Code Format
```cpp
#define ACTIVE_CODE "IOT_ACT_XXXXXXXXX"
#define DEVICE_TYPE "TEMPERATURE_HUMIDITY_SENSOR"
#define FIRMWARE_VERSION "1.0.0"
```

### Registration Message Format
```json
{
  "mac_address": "AA:BB:CC:DD:EE:FF",
  "device_name": "Temperature Sensor Lab A",
  "active_code": "IOT_ACT_123456789",
  "device_type": "TEMPERATURE_HUMIDITY_SENSOR",
  "firmware_version": "1.0.0",
  "sensors": "DHT22,BMP280",
  "capabilities": "temperature,humidity,pressure",
  "wifi_ssid": "PTIT_LAB_WIFI"
}
```

### Sensor Data Format
```json
{
  "device_id": "IOT_001",
  "timestamp": "2024-01-15T10:30:00Z",
  "sensors": {
    "temperature": 25.5,
    "humidity": 60.2,
    "pressure": 1013.25
  },
  "system": {
    "battery_level": 85,
    "signal_strength": -45,
    "free_heap": 150000
  }
}
```

## Environment Variables

```bash
# MQTT Configuration
MQTT_BROKER_URL=localhost:1883
MQTT_CLIENT_ID=device-service
MQTT_USERNAME=admin
MQTT_PASSWORD=admin

# Database (existing)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/device_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
```

## Dependencies

### Maven Dependencies đã thêm:
```xml
<!-- MQTT Dependencies -->
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-mqtt</artifactId>
</dependency>
<dependency>
    <groupId>org.eclipse.paho</groupId>
    <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
    <version>1.2.5</version>
</dependency>
```

## Testing

### Test MQTT Connection
```bash
# Install Mosquitto client
mosquitto_pub -h localhost -p 1883 -t "iot/devices/register" -m '{
  "mac_address": "AA:BB:CC:DD:EE:FF",
  "device_name": "Test Device",
  "active_code": "IOT_ACT_TEST123",
  "device_type": "TEMPERATURE_SENSOR",
  "firmware_version": "1.0.0",
  "sensors": "DHT22",
  "capabilities": "temperature,humidity",
  "wifi_ssid": "TEST_WIFI"
}'
```

### Test API Endpoints
```bash
# Get all IoT devices
curl -X GET "http://localhost:8082/device/api/iot-devices"

# Get active devices
curl -X GET "http://localhost:8082/device/api/iot-devices/active"

# Get dashboard overview
curl -X GET "http://localhost:8082/device/api/iot-devices/dashboard/overview"
```

## Deployment

### 1. Setup MQTT Broker (Mosquitto)
```bash
# Install Mosquitto
sudo apt-get install mosquitto mosquitto-clients

# Configure Mosquitto
sudo nano /etc/mosquitto/mosquitto.conf

# Add authentication
sudo mosquitto_passwd -c /etc/mosquitto/passwd admin

# Restart Mosquitto
sudo systemctl restart mosquitto
```

### 2. Database Migration
```sql
-- Tables will be created automatically by Hibernate
-- Make sure to backup existing data before running
```

### 3. Application Deployment
```bash
# Build application
mvn clean package

# Run with environment variables
java -jar target/device-service.jar
```

## Security Considerations

1. **MQTT Authentication**: Sử dụng username/password cho MQTT
2. **Active Code Validation**: Kiểm tra Active Code hợp lệ
3. **Data Encryption**: Encrypt sensitive data trong database
4. **Network Security**: VLAN isolation cho IoT devices
5. **Input Validation**: Validate tất cả input từ ESP32

## Troubleshooting

### Common Issues

1. **MQTT Connection Failed**
   - Kiểm tra MQTT broker đang chạy
   - Kiểm tra credentials trong application.yml
   - Kiểm tra network connectivity

2. **Device Not Registering**
   - Kiểm tra Active Code format
   - Kiểm tra MQTT topic subscription
   - Kiểm tra JSON message format

3. **Sensor Data Not Saving**
   - Kiểm tra database connection
   - Kiểm tra JSON parsing
   - Kiểm tra device exists và is IoT device

### Logs
```bash
# Check application logs
tail -f logs/device-service.log

# Check MQTT logs
tail -f /var/log/mosquitto/mosquitto.log
``` 
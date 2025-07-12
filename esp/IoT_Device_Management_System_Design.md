# Hệ thống Quản lý Thiết bị IoT WiFi (ESP32) - Thiết kế hệ thống

## 1. Tổng quan hệ thống

### 1.1 Mục tiêu

Nâng cấp hệ thống quản lý thiết bị hiện tại thành hệ thống quản lý thiết bị IoT WiFi sử dụng ESP32, cho phép:

- **Cấu hình WiFi qua Access Point Mode** của ESP32
- **Auto-registration** thiết bị IoT với Active Code
- **QR Code activation** để kích hoạt thiết bị
- Thu thập dữ liệu real-time từ thiết bị
- Hiển thị dashboard với các thông số môi trường (chỉ thiết bị đã active)
- Quản lý trạng thái và điều khiển thiết bị từ xa

### 1.2 Kiến trúc tổng thể

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   IoT Devices   │
│   (Dashboard)   │◄──►│   (Spring Boot) │◄──►│   (ESP32)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │ MQTT Broker     │
                       │ (Mosquitto)     │
                       └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Database      │
                       │   (PostgreSQL)  │
                       └─────────────────┘
```

## 2. Kịch bản sử dụng

### 2.1 Kịch bản 1: Cấu hình WiFi cho ESP32 (Access Point Mode)

**Actor:** ESP32 Device, Admin
**Precondition:** ESP32 chưa được cấu hình WiFi

**Flow:**

1. ESP32 khởi động ở chế độ Access Point (AP)
2. ESP32 tạo WiFi network với tên: "ESP32*Device*[MAC_Address]"
3. Admin kết nối vào WiFi network của ESP32
4. Admin truy cập http://192.168.4.1 (ESP32 web server)
5. Admin nhập thông tin cấu hình:
   - WiFi SSID của lab
   - WiFi Password của lab
   - Tên thiết bị (friendly name)
   - Mô tả thiết bị
6. ESP32 lưu thông tin vào EEPROM
7. ESP32 chuyển sang Station Mode và kết nối WiFi lab
8. ESP32 kết nối MQTT Broker

**Postcondition:** ESP32 đã được cấu hình WiFi và sẵn sàng kết nối server

### 2.2 Kịch bản 2: Auto-Registration với Active Code

**Actor:** ESP32 Device, MQTT Broker, Server
**Precondition:** ESP32 đã được cấu hình WiFi và kết nối MQTT

**Flow:**

1. ESP32 khởi động và kết nối WiFi lab (sử dụng credentials đã lưu)
2. ESP32 kết nối MQTT Broker
3. ESP32 gửi registration message đến topic `iot/devices/register`:
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
4. Server nhận registration message và:
   - Kiểm tra Active Code có hợp lệ không
   - Tạo device mới trong database với status "REGISTERED"
   - Generate device ID unique
   - Tạo cấu hình mặc định
5. Server gửi registration response đến topic `iot/devices/register/response`:
   ```json
   {
     "device_id": "IOT_001",
     "status": "registered",
     "data_interval": 30,
     "server_time": 1642234567890
   }
   ```
6. ESP32 nhận response và:
   - Lưu device ID
   - Subscribe vào topics riêng: `iot/devices/IOT_001/data`, `iot/devices/IOT_001/config`
   - Bắt đầu gửi sensor data
7. **Thiết bị chưa hiển thị trên dashboard** (status = "REGISTERED")

**Postcondition:** Thiết bị được auto-register với status "REGISTERED"

### 2.3 Kịch bản 3: QR Code Activation

**Actor:** Admin, Server, ESP32 Device
**Precondition:** Thiết bị đã được auto-register với status "REGISTERED"

**Flow:**

1. Server tạo QR code chứa Active Code: "IOT_ACT_123456789"
2. Admin in QR code và dán lên thiết bị ESP32
3. Admin truy cập vào trang quản lý thiết bị IoT
4. Admin thấy danh sách thiết bị "REGISTERED" (chưa active)
5. Admin nhấn "Scan QR Code" và quét QR code trên thiết bị
6. Web app trích xuất Active Code từ QR code
7. Web app gửi activation request đến server:
   ```json
   {
     "active_code": "IOT_ACT_123456789"
   }
   ```
8. Server tìm device với Active Code và đổi status thành "ACTIVE"
9. Server gửi activation command đến ESP32 qua topic `iot/devices/IOT_001/commands`:
   ```json
   {
     "command": "ACTIVATE",
     "config": {
       "device_name": "Temperature Sensor Lab A",
       "data_interval": 30,
       "alert_thresholds": {
         "temperature_min": 18,
         "temperature_max": 30,
         "humidity_min": 40,
         "humidity_max": 80
       }
     }
   }
   ```
10. ESP32 nhận command và cập nhật cấu hình
11. ESP32 gửi confirmation về server
12. **Thiết bị bây giờ hiển thị trên dashboard** (status = "ACTIVE")

**Postcondition:** Thiết bị được kích hoạt và hiển thị trên dashboard

### 2.4 Kịch bản 4: Thu thập và hiển thị dữ liệu (Chỉ thiết bị ACTIVE)

**Actor:** ESP32 Device, MQTT Broker, Server, Frontend
**Precondition:** Thiết bị đã được kích hoạt (status = "ACTIVE")

**Flow:**

1. ESP32 thu thập dữ liệu từ sensors theo interval đã cấu hình
2. ESP32 gửi dữ liệu đến MQTT topic `iot/devices/IOT_001/data`:
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
3. Server nhận dữ liệu qua MQTT và:
   - Validate dữ liệu
   - Lưu vào database
   - Cập nhật trạng thái thiết bị (last_seen)
   - Kiểm tra ngưỡng cảnh báo
4. Server gửi dữ liệu đến Frontend qua WebSocket
5. Frontend cập nhật dashboard real-time (chỉ thiết bị ACTIVE):
   - Hiển thị giá trị sensors
   - Cập nhật biểu đồ
   - Hiển thị trạng thái thiết bị

**Postcondition:** Dữ liệu được hiển thị real-time trên dashboard

### 2.5 Kịch bản 5: Quản lý thiết bị IoT

**Actor:** Admin/User
**Precondition:** Thiết bị đã được kích hoạt

**Flow:**

1. Admin truy cập dashboard quản lý thiết bị
2. Admin xem danh sách tất cả thiết bị IoT với trạng thái:
   - **REGISTERED**: Thiết bị mới được auto-register, chưa active
   - **ACTIVE**: Thiết bị đã kích hoạt, hiển thị trên dashboard
   - **OFFLINE**: Thiết bị không gửi data
   - **ERROR**: Thiết bị có lỗi
3. Admin có thể:
   - Quét QR code để kích hoạt thiết bị REGISTERED
   - Xem chi tiết thiết bị ACTIVE
   - Cấu hình lại thiết bị
   - Restart thiết bị
   - Update firmware
   - Deactivate thiết bị (ẩn khỏi dashboard)
   - Xóa thiết bị
4. Admin thực hiện action và hệ thống gửi command đến ESP32
5. ESP32 thực hiện command và gửi response về server
6. Server cập nhật trạng thái và thông báo cho frontend

**Postcondition:** Thiết bị được quản lý theo yêu cầu

## 3. Thiết kế hệ thống

### 3.1 Database Schema

#### 3.1.1 Bảng IoT_Devices

- `id`: Primary key
- `device_code`: Mã thiết bị unique (auto-generated)
- `device_name`: Tên thiết bị (friendly name)
- `active_code`: Active Code unique (có sẵn trong ESP32)
- `device_type`: Loại thiết bị (TEMPERATURE_SENSOR, HUMIDITY_SENSOR, etc.)
- `description`: Mô tả thiết bị
- `location`: Vị trí đặt thiết bị
- `mac_address`: MAC address của ESP32 (unique)
- `ip_address`: IP address hiện tại
- `firmware_version`: Phiên bản firmware
- `status`: Trạng thái (REGISTERED, ACTIVE, OFFLINE, ERROR, DEACTIVATED)
- `last_seen`: Thời gian cuối cùng nhận tín hiệu
- `wifi_ssid`: SSID WiFi đã cấu hình
- `activated_at`: Thời gian được kích hoạt (null nếu chưa active)
- `activated_by`: User ID người kích hoạt (null nếu chưa active)
- `created_at`: Thời gian tạo (auto-register)
- `updated_at`: Thời gian cập nhật cuối

#### 3.1.2 Bảng IoT_Sensor_Data

- `id`: Primary key
- `device_id`: Foreign key đến IoT_Devices
- `timestamp`: Thời gian thu thập dữ liệu
- `temperature`: Nhiệt độ (nếu có)
- `humidity`: Độ ẩm (nếu có)
- `pressure`: Áp suất (nếu có)
- `light`: Ánh sáng (nếu có)
- `battery_level`: Mức pin
- `signal_strength`: Cường độ tín hiệu WiFi
- `raw_data`: Dữ liệu thô từ sensor (JSON)

#### 3.1.3 Bảng IoT_Device_Config

- `id`: Primary key
- `device_id`: Foreign key đến IoT_Devices
- `data_interval`: Khoảng thời gian gửi dữ liệu (giây)
- `sensor_enabled`: Danh sách sensor được bật (JSON)
- `alert_thresholds`: Ngưỡng cảnh báo (JSON)
- `display_config`: Cấu hình hiển thị trên dashboard (JSON)
- `wifi_credentials`: Thông tin WiFi (encrypted JSON)
- `created_at`: Thời gian tạo
- `updated_at`: Thời gian cập nhật

#### 3.1.4 Bảng IoT_Device_Commands

- `id`: Primary key
- `device_id`: Foreign key đến IoT_Devices
- `command_type`: Loại lệnh (ACTIVATE, RESTART, UPDATE_FIRMWARE, CONFIGURE, DEACTIVATE)
- `command_data`: Dữ liệu lệnh (JSON)
- `status`: Trạng thái (PENDING, EXECUTING, COMPLETED, FAILED)
- `executed_at`: Thời gian thực thi
- `response`: Response từ device
- `created_by`: User ID người tạo lệnh
- `created_at`: Thời gian tạo

### 3.2 MQTT Topics Design

#### 3.2.1 Registration Topics

- `iot/devices/register` - ESP32 gửi registration message
- `iot/devices/register/response` - Server trả về registration info

#### 3.2.2 Device-specific Topics

- `iot/devices/{device_id}/data` - Device gửi sensor data
- `iot/devices/{device_id}/config` - Server gửi config updates
- `iot/devices/{device_id}/commands` - Server gửi commands
- `iot/devices/{device_id}/status` - Device gửi status updates

#### 3.2.3 System Topics

- `iot/system/health` - System health monitoring
- `iot/system/alerts` - System-wide alerts

### 3.3 API Design

#### 3.3.1 Device Management APIs

- `POST /api/iot-devices/register` - ESP32 gửi registration request
- `GET /api/iot-devices` - Lấy danh sách thiết bị (có filter theo status)
- `GET /api/iot-devices/registered` - Lấy danh sách thiết bị REGISTERED
- `GET /api/iot-devices/active` - Lấy danh sách thiết bị ACTIVE
- `GET /api/iot-devices/{id}` - Lấy chi tiết thiết bị
- `GET /api/iot-devices/active-code/{activeCode}` - Lấy device theo Active Code
- `PUT /api/iot-devices/{id}` - Cập nhật thông tin thiết bị
- `POST /api/iot-devices/activate` - Kích hoạt thiết bị bằng Active Code
- `POST /api/iot-devices/{id}/deactivate` - Vô hiệu hóa thiết bị
- `DELETE /api/iot-devices/{id}` - Xóa thiết bị

#### 3.3.2 QR Code APIs

- `POST /api/iot-devices/qr-scan` - Quét QR code và kích hoạt thiết bị
- `GET /api/iot-devices/{id}/qr-code` - Tạo QR code cho thiết bị

#### 3.3.3 Data Collection APIs

- `GET /api/iot-devices/{id}/data` - Lấy dữ liệu thiết bị (chỉ ACTIVE)
- `GET /api/iot-devices/{id}/data/latest` - Lấy dữ liệu mới nhất
- `GET /api/iot-devices/{id}/data/history` - Lấy lịch sử dữ liệu

#### 3.3.4 Device Control APIs

- `POST /api/iot-devices/{id}/commands/restart` - Restart thiết bị
- `POST /api/iot-devices/{id}/commands/configure` - Cấu hình thiết bị
- `POST /api/iot-devices/{id}/commands/update-firmware` - Update firmware
- `GET /api/iot-devices/{id}/commands` - Lấy danh sách lệnh

#### 3.3.5 Dashboard APIs

- `GET /api/dashboard/overview` - Tổng quan dashboard (chỉ thiết bị ACTIVE)
- `GET /api/dashboard/devices/status` - Trạng thái tất cả thiết bị
- `GET /api/dashboard/devices/{id}/metrics` - Metrics của thiết bị
- `GET /api/dashboard/alerts` - Danh sách cảnh báo

#### 3.4.2 Active Code Management

Mỗi ESP32 có Active Code unique được lưu trong EEPROM:

```cpp
// Active Code format: IOT_ACT_XXXXXXXXX
#define ACTIVE_CODE "IOT_ACT_123456789"
#define DEVICE_TYPE "TEMPERATURE_HUMIDITY_SENSOR"
#define FIRMWARE_VERSION "1.0.0"
```

#### 3.4.3 Registration Protocol

ESP32 sẽ gửi registration message khi khởi động:

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

#### 3.4.4 Data Transmission Protocol

ESP32 sẽ gửi dữ liệu theo format:

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
    "free_heap": 150000,
    "uptime": 3600,
    "status": "ACTIVE"
  }
}
```

### 3.5 Frontend Dashboard Design

#### 3.5.1 Dashboard Layout (Chỉ hiển thị thiết bị ACTIVE)

```
┌─────────────────────────────────────────────────────────────┐
│                    IoT Device Dashboard                     │
├─────────────────────────────────────────────────────────────┤
│  Overview Cards                                             │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │ Total   │ │ Active  │ │ Registered │ Alerts  │           │
│  │ Devices │ │ Devices │ │ Devices │         │           │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │
├─────────────────────────────────────────────────────────────┤
│  Device Management                    │  Real-time Charts   │
│  ┌─────────────────────────┐         │  ┌─────────────────┐ │
│  │ Active Devices:         │         │  │ Temperature     │ │
│  │ • Temp Sensor 1         │         │  │ Chart           │ │
│  │ • Humidity Sensor 2     │         │  │                 │ │
│  │                         │         │  └─────────────────┘ │
│  │ Registered Devices:     │         │  ┌─────────────────┐ │
│  │ • New Device (Scan QR)  │         │  │ Humidity Chart  │ │
│  │ • New Device (Scan QR)  │         │  │                 │ │
│  └─────────────────────────┘         │  └─────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

#### 3.5.2 Device Management View

```
┌─────────────────────────────────────────────────────────────┐
│  Device Management                                          │
├─────────────────────────────────────────────────────────────┤
│  Active Devices (Hiển thị trên Dashboard)                  │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Device: Temperature Sensor 001                          │ │
│  │ Status: Online    Battery: 85%    Signal: -45dBm        │ │
│  │ [View Details] [Configure] [Deactivate] [Delete]        │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                             │
│  Registered Devices (Chưa hiển thị trên Dashboard)         │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Device: IOT_002 (Auto-registered)                       │ │
│  │ MAC: AA:BB:CC:DD:EE:FF    Type: Humidity Sensor        │ │
│  │ Active Code: IOT_ACT_123456789                          │ │
│  │ [Scan QR Code] [View Details] [Delete]                  │ │
│  └─────────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Device: IOT_003 (Auto-registered)                       │ │
│  │ MAC: FF:EE:DD:CC:BB:AA    Type: Pressure Sensor        │ │
│  │ Active Code: IOT_ACT_987654321                          │ │
│  │ [Scan QR Code] [View Details] [Delete]                  │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

#### 3.5.3 QR Code Scanner Interface

```
┌─────────────────────────────────────────────────────────────┐
│  QR Code Scanner                                            │
├─────────────────────────────────────────────────────────────┤
│  Scan QR Code to Activate Device                            │
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                                                         │ │
│  │                    Camera View                          │ │
│  │                                                         │
│  │                                                         │ │
│  │                                                         │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                             │
│  Instructions:                                              │
│  • Point camera at QR code on device                       │ │
│  • QR code contains Active Code                            │ │
│  • Device will be activated automatically                  │ │
│                                                             │
│  [Cancel] [Manual Input]                                    │
└─────────────────────────────────────────────────────────────┘
```

#### 3.5.4 Manual Active Code Input

```
┌─────────────────────────────────────────────────────────────┐
│  Manual Active Code Input                                   │
├─────────────────────────────────────────────────────────────┤
│  Enter Active Code from device:                             │
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Active Code: [IOT_ACT_123456789]                        │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                             │
│  Device Information:                                        │
│  • MAC Address: AA:BB:CC:DD:EE:FF                          │
│  • Type: Humidity Sensor                                    │ │
│  • Firmware: 1.0.0                                          │ │
│                                                             │
│  [Cancel] [Activate Device]                                 │
└─────────────────────────────────────────────────────────────┘
```

## 4. Security Considerations

### 4.1 Authentication & Authorization

- Device authentication sử dụng Active Code và MAC address
- MQTT authentication với username/password
- API authentication cho admin/user
- Role-based access control
- Secure QR code generation và scanning

### 4.2 Data Security

- Encrypt sensitive data trong database
- MQTT over TLS/SSL
- Input validation và sanitization
- Rate limiting cho MQTT messages
- Active Code validation

### 4.3 Network Security

- VLAN isolation cho IoT devices
- MQTT broker security
- Network monitoring
- Intrusion detection
- WiFi credentials encryption

## 5. Performance & Scalability

### 5.1 MQTT Broker Optimization

- MQTT broker clustering
- Message persistence
- QoS levels management
- Topic wildcards optimization

### 5.2 Database Optimization

- Indexing cho frequently queried fields
- Partitioning cho sensor data tables
- Data archiving strategy
- Connection pooling

### 5.3 Caching Strategy

- Redis cache cho real-time data
- In-memory caching cho device status
- MQTT message caching

## 6. Monitoring & Alerting

### 6.1 System Monitoring

- MQTT broker health monitoring
- Server health monitoring
- Database performance monitoring
- Network connectivity monitoring

### 6.2 Device Monitoring

- Device online/offline status
- MQTT message monitoring
- Battery level monitoring
- Sensor accuracy monitoring

### 6.3 Alerting System

- Email notifications
- SMS alerts
- Webhook integrations
- Dashboard notifications

## 7. Deployment Strategy

### 7.1 MQTT Broker Setup

- Mosquitto broker installation
- SSL/TLS configuration
- Authentication setup
- Clustering configuration

### 7.2 Development Environment

- Local MQTT broker setup
- Docker containers
- Database seeding
- Mock IoT devices

### 7.3 Production Environment

- Cloud deployment (AWS/Azure/GCP)
- MQTT broker clustering
- CI/CD pipeline
- Automated backups

## 8. Testing Strategy

### 8.1 Unit Testing

- Service layer testing
- MQTT message handling testing
- Repository layer testing
- API endpoint testing

### 8.2 Integration Testing

- MQTT integration testing
- API integration testing
- Database integration testing
- IoT device integration testing

### 8.3 End-to-End Testing

- Complete WiFi configuration flow
- Device registration flow
- QR code activation flow
- Data collection flow
- Dashboard functionality

## 9. Future Enhancements

### 9.1 Advanced Features

- Machine learning cho predictive maintenance
- Advanced analytics và reporting
- Mobile app development
- Voice control integration

### 9.2 IoT Protocol Support

- CoAP protocol support
- LoRaWAN integration
- 5G IoT support
- Edge computing

### 9.3 Device Management

- Bulk device activation
- Device templates
- Firmware management
- Remote configuration

## 10. Implementation Timeline

### Phase 1 (Weeks 1-4): Foundation

- MQTT broker setup và configuration
- Database schema design và implementation
- ESP32 Access Point Mode implementation
- Basic MQTT client implementation

### Phase 2 (Weeks 5-8): Core Features

- WiFi configuration web interface
- Device registration system
- Active Code management
- QR code generation

### Phase 3 (Weeks 9-12): Advanced Features

- QR code scanner implementation
- Device activation workflow
- Dashboard implementation
- Real-time data display

### Phase 4 (Weeks 13-16): Testing & Deployment

- Comprehensive testing
- Production deployment
- Documentation
- User training

### 11.11 Configuration HTML Page

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>ESP32 IoT Device Configuration</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        max-width: 600px;
        margin: 0 auto;
        padding: 20px;
        background-color: #f5f5f5;
      }
      .container {
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      }
      h1 {
        color: #333;
        text-align: center;
        margin-bottom: 30px;
      }
      .form-group {
        margin-bottom: 20px;
      }
      label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
        color: #555;
      }
      input[type="text"],
      input[type="password"],
      textarea {
        width: 100%;
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 5px;
        font-size: 16px;
        box-sizing: border-box;
      }
      textarea {
        height: 80px;
        resize: vertical;
      }
      button {
        background-color: #007bff;
        color: white;
        padding: 12px 30px;
        border: none;
        border-radius: 5px;
        font-size: 16px;
        cursor: pointer;
        width: 100%;
      }
      button:hover {
        background-color: #0056b3;
      }
      .info {
        background-color: #e7f3ff;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        border-left: 4px solid #007bff;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <h1>ESP32 IoT Device Configuration</h1>

      <div class="info">
        <strong>Device Information:</strong><br />
        MAC Address: <span id="mac-address">Loading...</span><br />
        Device Type: <span id="device-type">Loading...</span><br />
        Active Code: <span id="active-code">Loading...</span>
      </div>

      <form id="config-form">
        <div class="form-group">
          <label for="ssid">WiFi Network Name (SSID):</label>
          <input type="text" id="ssid" name="ssid" required />
        </div>

        <div class="form-group">
          <label for="password">WiFi Password:</label>
          <input type="password" id="password" name="password" required />
        </div>

        <div class="form-group">
          <label for="device_name">Device Name:</label>
          <input
            type="text"
            id="device_name"
            name="device_name"
            placeholder="e.g., Temperature Sensor Lab A"
            required
          />
        </div>

        <div class="form-group">
          <label for="device_description">Device Description:</label>
          <textarea
            id="device_description"
            name="device_description"
            placeholder="e.g., Monitor temperature and humidity in Lab A"
          ></textarea>
        </div>

        <button type="submit">Save Configuration</button>
      </form>
    </div>

    <script>
      // Display device information
      document.getElementById("mac-address").textContent = "AA:BB:CC:DD:EE:FF";
      document.getElementById("device-type").textContent =
        "TEMPERATURE_HUMIDITY_SENSOR";
      document.getElementById("active-code").textContent = "IOT_ACT_123456789";

      // Handle form submission
      document
        .getElementById("config-form")
        .addEventListener("submit", function (e) {
          e.preventDefault();

          const formData = new FormData(this);

          fetch("/save-config", {
            method: "POST",
            body: formData,
          })
            .then((response) => response.text())
            .then((data) => {
              alert(data);
              if (data.includes("successfully")) {
                // Show restart message
                document.body.innerHTML = `
                        <div class="container">
                            <h1>Configuration Saved!</h1>
                            <div class="info">
                                <p>Your device configuration has been saved successfully.</p>
                                <p>The device will now restart and attempt to connect to the specified WiFi network.</p>
                                <p>You can close this page and disconnect from the ESP32 WiFi network.</p>
                            </div>
                        </div>
                    `;
              }
            })
            .catch((error) => {
              alert("Error saving configuration: " + error);
            });
        });
    </script>
  </body>
</html>
```

#### 11.18.4 Testing Workflow

1. Upload firmware lên ESP32
2. ESP32 khởi động ở AP mode
3. Kết nối vào WiFi "ESP32*Device*[MAC]"
4. Truy cập http://192.168.4.1
5. Cấu hình WiFi và thông tin thiết bị
6. ESP32 kết nối WiFi và gửi registration message
7. Kiểm tra MQTT messages trên broker
8. Test QR code activation

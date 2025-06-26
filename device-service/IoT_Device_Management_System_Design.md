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
2. ESP32 tạo WiFi network với tên: "ESP32_Device_[MAC_Address]"
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

### 3.4 ESP32 Firmware Design

#### 3.4.1 Cấu trúc firmware
```
ESP32 Firmware
├── WiFi Management
│   ├── Access Point Mode
│   ├── Station Mode
│   ├── Network Configuration
│   └── Connection Recovery
├── Web Server (AP Mode)
│   ├── Configuration Page
│   ├── WiFi Setup Form
│   └── Status Display
├── MQTT Communication
│   ├── MQTT Client
│   ├── Topic Management
│   ├── Message Handling
│   └── Auto-reconnection
├── Device Registration
│   ├── Active Code Management
│   ├── Registration Message
│   └── Device ID Management
├── Sensor Management
│   ├── Sensor Initialization
│   ├── Data Collection
│   └── Sensor Calibration
├── Command Processing
│   ├── Command Parser
│   ├── Command Executor
│   └── Response Handler
├── Configuration Management
│   ├── EEPROM Storage
│   ├── Config Validation
│   └── Config Updates
└── System
    ├── OTA Updates
    ├── Error Handling
    └── Power Management
```

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

## 11. ESP32 Firmware Implementation

### 11.1 Cấu trúc thư mục ESP32
```
ESP32_IoT_Device/
├── src/
│   ├── main.cpp                 # Main application
│   ├── config/
│   │   ├── wifi_config.h        # WiFi configuration
│   │   ├── mqtt_config.h        # MQTT configuration
│   │   └── device_config.h      # Device configuration
│   ├── wifi/
│   │   ├── wifi_manager.h       # WiFi management
│   │   └── wifi_manager.cpp     # WiFi implementation
│   ├── mqtt/
│   │   ├── mqtt_client.h        # MQTT client
│   │   └── mqtt_client.cpp      # MQTT implementation
│   ├── web_server/
│   │   ├── web_server.h         # Web server for AP mode
│   │   └── web_server.cpp       # Web server implementation
│   ├── sensors/
│   │   ├── sensor_manager.h     # Sensor management
│   │   └── sensor_manager.cpp   # Sensor implementation
│   ├── commands/
│   │   ├── command_handler.h    # Command processing
│   │   └── command_handler.cpp  # Command implementation
│   └── utils/
│       ├── eeprom_manager.h     # EEPROM storage
│       └── eeprom_manager.cpp   # EEPROM implementation
├── data/
│   └── config.html              # WiFi configuration page
├── platformio.ini               # PlatformIO configuration
└── README.md                    # Firmware documentation
```

### 11.2 PlatformIO Configuration (platformio.ini)
```ini
[env:esp32dev]
platform = espressif32
board = esp32dev
framework = arduino
monitor_speed = 115200
upload_speed = 921600

; Libraries
lib_deps = 
    bblanchon/ArduinoJson @ ^6.21.3
    knolleary/PubSubClient @ ^2.8
    adafruit/DHT sensor library @ ^1.4.4
    adafruit/Adafruit Unified Sensor @ ^1.1.9
    adafruit/Adafruit BMP280 Library @ ^2.6.6
    me-no-dev/ESPAsyncWebServer @ ^1.2.3
    me-no-dev/AsyncTCP @ ^1.1.1

; Build flags
build_flags = 
    -DCORE_DEBUG_LEVEL=3
    -DASYNC_TCP_SSL_ENABLED=1
    -DWEBSERVER_SSL_ENABLED=1

; Monitor filters
monitor_filters = esp32_exception_decoder
```

### 11.3 Main Application (main.cpp)
```cpp
#include <Arduino.h>
#include <WiFi.h>
#include <EEPROM.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>
#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <SPIFFS.h>

#include "config/wifi_config.h"
#include "config/mqtt_config.h"
#include "config/device_config.h"
#include "wifi/wifi_manager.h"
#include "mqtt/mqtt_client.h"
#include "web_server/web_server.h"
#include "sensors/sensor_manager.h"
#include "commands/command_handler.h"
#include "utils/eeprom_manager.h"

// Global objects
WiFiManager wifiManager;
MqttClient mqttClient;
WebServer webServer;
SensorManager sensorManager;
CommandHandler commandHandler;
EepromManager eepromManager;

// Device state
bool isConfigured = false;
bool isRegistered = false;
String deviceId = "";
unsigned long lastDataSend = 0;
unsigned long lastHeartbeat = 0;

void setup() {
    Serial.begin(115200);
    Serial.println("ESP32 IoT Device Starting...");
    
    // Initialize EEPROM
    eepromManager.begin();
    
    // Initialize SPIFFS
    if (!SPIFFS.begin(true)) {
        Serial.println("SPIFFS Mount Failed");
        return;
    }
    
    // Initialize sensors
    sensorManager.begin();
    
    // Check if device is configured
    isConfigured = eepromManager.isWifiConfigured();
    
    if (isConfigured) {
        Serial.println("Device is configured, connecting to WiFi...");
        if (wifiManager.connectToWifi()) {
            Serial.println("WiFi connected successfully");
            
            // Initialize MQTT
            mqttClient.begin();
            
            // Register device
            registerDevice();
        } else {
            Serial.println("Failed to connect to WiFi, starting AP mode");
            startAccessPointMode();
        }
    } else {
        Serial.println("Device not configured, starting AP mode");
        startAccessPointMode();
    }
    
    // Initialize web server
    webServer.begin();
}

void loop() {
    // Handle web server
    webServer.handle();
    
    // Handle MQTT
    mqttClient.loop();
    
    // Handle commands
    commandHandler.processCommands();
    
    // Send sensor data if registered and active
    if (isRegistered && !deviceId.isEmpty()) {
        unsigned long currentTime = millis();
        
        // Send sensor data
        if (currentTime - lastDataSend >= DATA_SEND_INTERVAL) {
            sendSensorData();
            lastDataSend = currentTime;
        }
        
        // Send heartbeat
        if (currentTime - lastHeartbeat >= HEARTBEAT_INTERVAL) {
            sendHeartbeat();
            lastHeartbeat = currentTime;
        }
    }
    
    // Check WiFi connection
    if (isConfigured && WiFi.status() != WL_CONNECTED) {
        Serial.println("WiFi disconnected, attempting to reconnect...");
        wifiManager.reconnect();
    }
    
    delay(100);
}

void startAccessPointMode() {
    Serial.println("Starting Access Point mode...");
    wifiManager.startAccessPoint();
    webServer.startConfigurationMode();
}

void registerDevice() {
    Serial.println("Registering device...");
    
    // Create registration message
    DynamicJsonDocument doc(1024);
    doc["mac_address"] = WiFi.macAddress();
    doc["device_name"] = eepromManager.getDeviceName();
    doc["active_code"] = ACTIVE_CODE;
    doc["device_type"] = DEVICE_TYPE;
    doc["firmware_version"] = FIRMWARE_VERSION;
    doc["sensors"] = sensorManager.getSensorList();
    doc["capabilities"] = sensorManager.getCapabilities();
    doc["wifi_ssid"] = eepromManager.getWifiSsid();
    
    String registrationMessage;
    serializeJson(doc, registrationMessage);
    
    // Send registration message
    if (mqttClient.publish("iot/devices/register", registrationMessage)) {
        Serial.println("Registration message sent");
    } else {
        Serial.println("Failed to send registration message");
    }
}

void sendSensorData() {
    if (deviceId.isEmpty()) return;
    
    // Get sensor data
    DynamicJsonDocument doc(1024);
    doc["device_id"] = deviceId;
    doc["timestamp"] = getCurrentTimestamp();
    
    // Add sensor data
    JsonObject sensors = doc.createNestedObject("sensors");
    sensorManager.readSensors(sensors);
    
    // Add system data
    JsonObject system = doc.createNestedObject("system");
    system["battery_level"] = getBatteryLevel();
    system["signal_strength"] = WiFi.RSSI();
    system["free_heap"] = ESP.getFreeHeap();
    system["uptime"] = millis() / 1000;
    system["status"] = "ACTIVE";
    
    String sensorMessage;
    serializeJson(doc, sensorMessage);
    
    String topic = "iot/devices/" + deviceId + "/data";
    if (mqttClient.publish(topic.c_str(), sensorMessage)) {
        Serial.println("Sensor data sent");
    } else {
        Serial.println("Failed to send sensor data");
    }
}

void sendHeartbeat() {
    if (deviceId.isEmpty()) return;
    
    DynamicJsonDocument doc(256);
    doc["device_id"] = deviceId;
    doc["timestamp"] = getCurrentTimestamp();
    doc["status"] = "ONLINE";
    doc["free_heap"] = ESP.getFreeHeap();
    doc["uptime"] = millis() / 1000;
    
    String heartbeatMessage;
    serializeJson(doc, heartbeatMessage);
    
    String topic = "iot/devices/" + deviceId + "/status";
    mqttClient.publish(topic.c_str(), heartbeatMessage);
}

String getCurrentTimestamp() {
    // Get current time from NTP or use millis() as fallback
    unsigned long currentTime = millis() / 1000;
    return String(currentTime);
}

int getBatteryLevel() {
    // Implement battery level reading
    // For now, return a dummy value
    return 85;
}
```

### 11.4 Device Configuration (config/device_config.h)
```cpp
#ifndef DEVICE_CONFIG_H
#define DEVICE_CONFIG_H

// Device Information
#define DEVICE_TYPE "TEMPERATURE_HUMIDITY_SENSOR"
#define FIRMWARE_VERSION "1.0.0"
#define ACTIVE_CODE "IOT_ACT_123456789"

// Timing Configuration
#define DATA_SEND_INTERVAL 30000    // 30 seconds
#define HEARTBEAT_INTERVAL 60000    // 60 seconds
#define WIFI_TIMEOUT 30000          // 30 seconds
#define MQTT_TIMEOUT 10000          // 10 seconds

// WiFi Configuration
#define AP_SSID_PREFIX "ESP32_Device_"
#define AP_PASSWORD "12345678"
#define AP_IP "192.168.4.1"
#define AP_GATEWAY "192.168.4.1"
#define AP_SUBNET "255.255.255.0"

// MQTT Configuration
#define MQTT_BROKER "your-mqtt-broker.com"
#define MQTT_PORT 1883
#define MQTT_USERNAME "your-username"
#define MQTT_PASSWORD "your-password"
#define MQTT_CLIENT_ID_PREFIX "ESP32_"

// EEPROM Configuration
#define EEPROM_SIZE 512
#define WIFI_SSID_ADDR 0
#define WIFI_PASSWORD_ADDR 32
#define DEVICE_NAME_ADDR 64
#define DEVICE_DESCRIPTION_ADDR 128

// Sensor Configuration
#define DHT_PIN 4
#define DHT_TYPE DHT22
#define BMP280_SDA_PIN 21
#define BMP280_SCL_PIN 22

// Web Server Configuration
#define WEB_SERVER_PORT 80

#endif
```

### 11.5 WiFi Manager (wifi/wifi_manager.h)
```cpp
#ifndef WIFI_MANAGER_H
#define WIFI_MANAGER_H

#include <WiFi.h>
#include <WiFiManager.h>

class WiFiManager {
private:
    WiFiManager wifiManager;
    bool isApMode = false;
    
public:
    WiFiManager();
    bool connectToWifi();
    void startAccessPoint();
    bool reconnect();
    bool isConnected();
    String getMacAddress();
    int getSignalStrength();
    void disconnect();
};

#endif
```

### 11.6 WiFi Manager Implementation (wifi/wifi_manager.cpp)
```cpp
#include "wifi_manager.h"
#include "config/device_config.h"
#include "utils/eeprom_manager.h"

WiFiManager::WiFiManager() {
    // Configure WiFiManager
    wifiManager.setConfigPortalTimeout(WIFI_TIMEOUT / 1000);
    wifiManager.setConnectTimeout(WIFI_TIMEOUT / 1000);
    wifiManager.setBreakAfterConfig(true);
}

bool WiFiManager::connectToWifi() {
    EepromManager eepromManager;
    String ssid = eepromManager.getWifiSsid();
    String password = eepromManager.getWifiPassword();
    
    if (ssid.isEmpty()) {
        Serial.println("No WiFi credentials stored");
        return false;
    }
    
    Serial.print("Connecting to WiFi: ");
    Serial.println(ssid);
    
    WiFi.begin(ssid.c_str(), password.c_str());
    
    int attempts = 0;
    while (WiFi.status() != WL_CONNECTED && attempts < 20) {
        delay(500);
        Serial.print(".");
        attempts++;
    }
    
    if (WiFi.status() == WL_CONNECTED) {
        Serial.println();
        Serial.println("WiFi connected");
        Serial.print("IP address: ");
        Serial.println(WiFi.localIP());
        isApMode = false;
        return true;
    } else {
        Serial.println();
        Serial.println("Failed to connect to WiFi");
        return false;
    }
}

void WiFiManager::startAccessPoint() {
    String macAddress = WiFi.macAddress();
    String ssid = AP_SSID_PREFIX + macAddress.substring(12);
    
    Serial.print("Starting Access Point: ");
    Serial.println(ssid);
    
    WiFi.mode(WIFI_AP);
    WiFi.softAP(ssid.c_str(), AP_PASSWORD);
    
    // Configure AP IP
    IPAddress localIP;
    localIP.fromString(AP_IP);
    IPAddress gateway;
    gateway.fromString(AP_GATEWAY);
    IPAddress subnet;
    subnet.fromString(AP_SUBNET);
    
    WiFi.softAPConfig(localIP, gateway, subnet);
    
    Serial.print("AP IP address: ");
    Serial.println(WiFi.softAPIP());
    
    isApMode = true;
}

bool WiFiManager::reconnect() {
    if (isApMode) {
        return false;
    }
    
    Serial.println("Attempting to reconnect to WiFi...");
    WiFi.disconnect();
    delay(1000);
    return connectToWifi();
}

bool WiFiManager::isConnected() {
    return WiFi.status() == WL_CONNECTED;
}

String WiFiManager::getMacAddress() {
    return WiFi.macAddress();
}

int WiFiManager::getSignalStrength() {
    return WiFi.RSSI();
}

void WiFiManager::disconnect() {
    WiFi.disconnect();
}
```

### 11.7 MQTT Client (mqtt/mqtt_client.h)
```cpp
#ifndef MQTT_CLIENT_H
#define MQTT_CLIENT_H

#include <PubSubClient.h>
#include <WiFi.h>

class MqttClient {
private:
    PubSubClient client;
    String clientId;
    bool isConnected = false;
    
    static void messageCallback(char* topic, byte* payload, unsigned int length);
    
public:
    MqttClient();
    void begin();
    bool connect();
    void disconnect();
    bool publish(const char* topic, const char* message);
    bool subscribe(const char* topic);
    void loop();
    bool isConnected();
    void setCallback(void (*callback)(char*, byte*, unsigned int));
};

#endif
```

### 11.8 MQTT Client Implementation (mqtt/mqtt_client.cpp)
```cpp
#include "mqtt_client.h"
#include "config/mqtt_config.h"
#include "config/device_config.h"
#include "commands/command_handler.h"

MqttClient::MqttClient() {
    clientId = MQTT_CLIENT_ID_PREFIX + WiFi.macAddress();
    client.setClient(WiFi);
    client.setServer(MQTT_BROKER, MQTT_PORT);
    client.setCallback(messageCallback);
}

void MqttClient::begin() {
    Serial.println("Initializing MQTT client...");
}

bool MqttClient::connect() {
    Serial.print("Connecting to MQTT broker: ");
    Serial.println(MQTT_BROKER);
    
    if (client.connect(clientId.c_str(), MQTT_USERNAME, MQTT_PASSWORD)) {
        Serial.println("MQTT connected");
        isConnected = true;
        
        // Subscribe to device-specific topics
        String deviceTopic = "iot/devices/" + deviceId + "/#";
        subscribe(deviceTopic.c_str());
        
        return true;
    } else {
        Serial.print("MQTT connection failed, rc=");
        Serial.println(client.state());
        isConnected = false;
        return false;
    }
}

void MqttClient::disconnect() {
    client.disconnect();
    isConnected = false;
}

bool MqttClient::publish(const char* topic, const char* message) {
    if (!isConnected) {
        if (!connect()) {
            return false;
        }
    }
    
    return client.publish(topic, message);
}

bool MqttClient::subscribe(const char* topic) {
    if (!isConnected) {
        return false;
    }
    
    return client.subscribe(topic);
}

void MqttClient::loop() {
    if (!client.loop()) {
        if (WiFi.isConnected()) {
            Serial.println("MQTT connection lost, attempting to reconnect...");
            connect();
        }
    }
}

bool MqttClient::isConnected() {
    return client.connected();
}

void MqttClient::setCallback(void (*callback)(char*, byte*, unsigned int)) {
    client.setCallback(callback);
}

void MqttClient::messageCallback(char* topic, byte* payload, unsigned int length) {
    Serial.print("Message received on topic: ");
    Serial.println(topic);
    
    // Convert payload to string
    char message[length + 1];
    memcpy(message, payload, length);
    message[length] = '\0';
    
    Serial.print("Message: ");
    Serial.println(message);
    
    // Handle different message types
    String topicStr = String(topic);
    
    if (topicStr.endsWith("/commands")) {
        // Handle command message
        CommandHandler::processCommand(message);
    } else if (topicStr.endsWith("/config")) {
        // Handle configuration message
        // TODO: Implement configuration handling
    } else if (topicStr.endsWith("/register/response")) {
        // Handle registration response
        handleRegistrationResponse(message);
    }
}

void handleRegistrationResponse(const char* message) {
    DynamicJsonDocument doc(512);
    DeserializationError error = deserializeJson(doc, message);
    
    if (error) {
        Serial.println("Failed to parse registration response");
        return;
    }
    
    if (doc.containsKey("device_id")) {
        deviceId = doc["device_id"].as<String>();
        Serial.print("Device registered with ID: ");
        Serial.println(deviceId);
        isRegistered = true;
        
        // Subscribe to device-specific topics
        String dataTopic = "iot/devices/" + deviceId + "/data";
        String configTopic = "iot/devices/" + deviceId + "/config";
        String commandsTopic = "iot/devices/" + deviceId + "/commands";
        
        mqttClient.subscribe(dataTopic.c_str());
        mqttClient.subscribe(configTopic.c_str());
        mqttClient.subscribe(commandsTopic.c_str());
    }
}
```

### 11.9 Web Server (web_server/web_server.h)
```cpp
#ifndef WEB_SERVER_H
#define WEB_SERVER_H

#include <ESPAsyncWebServer.h>
#include <SPIFFS.h>

class WebServer {
private:
    AsyncWebServer server;
    bool isConfigurationMode = false;
    
public:
    WebServer();
    void begin();
    void startConfigurationMode();
    void stopConfigurationMode();
    void handleRoot(AsyncWebServerRequest *request);
    void handleConfigure(AsyncWebServerRequest *request);
    void handleSaveConfig(AsyncWebServerRequest *request);
};

#endif
```

### 11.10 Web Server Implementation (web_server/web_server.cpp)
```cpp
#include "web_server.h"
#include "config/device_config.h"
#include "utils/eeprom_manager.h"

WebServer::WebServer() : server(WEB_SERVER_PORT) {
}

void WebServer::begin() {
    // Serve static files from SPIFFS
    server.serveStatic("/", SPIFFS, "/").setDefaultFile("config.html");
    
    // Handle configuration form
    server.on("/configure", HTTP_GET, [this](AsyncWebServerRequest *request) {
        handleConfigure(request);
    });
    
    server.on("/save-config", HTTP_POST, [this](AsyncWebServerRequest *request) {
        handleSaveConfig(request);
    });
    
    server.begin();
    Serial.println("Web server started");
}

void WebServer::startConfigurationMode() {
    isConfigurationMode = true;
    Serial.println("Configuration mode enabled");
}

void WebServer::stopConfigurationMode() {
    isConfigurationMode = false;
    Serial.println("Configuration mode disabled");
}

void WebServer::handleConfigure(AsyncWebServerRequest *request) {
    request->send(SPIFFS, "/config.html", "text/html");
}

void WebServer::handleSaveConfig(AsyncWebServerRequest *request) {
    if (request->hasParam("ssid", true) && request->hasParam("password", true) && 
        request->hasParam("device_name", true)) {
        
        String ssid = request->getParam("ssid", true)->value();
        String password = request->getParam("password", true)->value();
        String deviceName = request->getParam("device_name", true)->value();
        String deviceDescription = request->getParam("device_description", true)->value();
        
        // Save to EEPROM
        EepromManager eepromManager;
        eepromManager.saveWifiCredentials(ssid, password);
        eepromManager.saveDeviceInfo(deviceName, deviceDescription);
        
        // Send success response
        request->send(200, "text/plain", "Configuration saved successfully. Device will restart.");
        
        // Restart device after a delay
        delay(2000);
        ESP.restart();
    } else {
        request->send(400, "text/plain", "Missing required parameters");
    }
}
```

### 11.11 Configuration HTML Page (data/config.html)
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
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
        input[type="text"], input[type="password"], textarea {
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
            <strong>Device Information:</strong><br>
            MAC Address: <span id="mac-address">Loading...</span><br>
            Device Type: <span id="device-type">Loading...</span><br>
            Active Code: <span id="active-code">Loading...</span>
        </div>
        
        <form id="config-form">
            <div class="form-group">
                <label for="ssid">WiFi Network Name (SSID):</label>
                <input type="text" id="ssid" name="ssid" required>
            </div>
            
            <div class="form-group">
                <label for="password">WiFi Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <div class="form-group">
                <label for="device_name">Device Name:</label>
                <input type="text" id="device_name" name="device_name" 
                       placeholder="e.g., Temperature Sensor Lab A" required>
            </div>
            
            <div class="form-group">
                <label for="device_description">Device Description:</label>
                <textarea id="device_description" name="device_description" 
                          placeholder="e.g., Monitor temperature and humidity in Lab A"></textarea>
            </div>
            
            <button type="submit">Save Configuration</button>
        </form>
    </div>
    
    <script>
        // Display device information
        document.getElementById('mac-address').textContent = 'AA:BB:CC:DD:EE:FF';
        document.getElementById('device-type').textContent = 'TEMPERATURE_HUMIDITY_SENSOR';
        document.getElementById('active-code').textContent = 'IOT_ACT_123456789';
        
        // Handle form submission
        document.getElementById('config-form').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            
            fetch('/save-config', {
                method: 'POST',
                body: formData
            })
            .then(response => response.text())
            .then(data => {
                alert(data);
                if (data.includes('successfully')) {
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
            .catch(error => {
                alert('Error saving configuration: ' + error);
            });
        });
    </script>
</body>
</html>
```

### 11.12 Sensor Manager (sensors/sensor_manager.h)
```cpp
#ifndef SENSOR_MANAGER_H
#define SENSOR_MANAGER_H

#include <DHT.h>
#include <Adafruit_BMP280.h>
#include <ArduinoJson.h>

class SensorManager {
private:
    DHT dht;
    Adafruit_BMP280 bmp;
    bool dhtAvailable = false;
    bool bmpAvailable = false;
    
public:
    SensorManager();
    void begin();
    void readSensors(JsonObject& sensors);
    String getSensorList();
    String getCapabilities();
    float getTemperature();
    float getHumidity();
    float getPressure();
    bool isDhtAvailable();
    bool isBmpAvailable();
};

#endif
```

### 11.13 Sensor Manager Implementation (sensors/sensor_manager.cpp)
```cpp
#include "sensor_manager.h"
#include "config/device_config.h"

SensorManager::SensorManager() : dht(DHT_PIN, DHT_TYPE) {
}

void SensorManager::begin() {
    Serial.println("Initializing sensors...");
    
    // Initialize DHT sensor
    dht.begin();
    delay(2000); // Wait for sensor to stabilize
    
    // Check DHT sensor
    float temp = dht.readTemperature();
    if (!isnan(temp)) {
        dhtAvailable = true;
        Serial.println("DHT sensor initialized successfully");
    } else {
        Serial.println("DHT sensor not found");
    }
    
    // Initialize BMP280 sensor
    if (bmp.begin(BMP280_SDA_PIN, BMP280_SCL_PIN)) {
        bmpAvailable = true;
        Serial.println("BMP280 sensor initialized successfully");
    } else {
        Serial.println("BMP280 sensor not found");
    }
}

void SensorManager::readSensors(JsonObject& sensors) {
    // Read temperature from DHT
    if (dhtAvailable) {
        float temp = dht.readTemperature();
        if (!isnan(temp)) {
            sensors["temperature"] = temp;
        }
        
        float humidity = dht.readHumidity();
        if (!isnan(humidity)) {
            sensors["humidity"] = humidity;
        }
    }
    
    // Read pressure from BMP280
    if (bmpAvailable) {
        float pressure = bmp.readPressure() / 100.0; // Convert to hPa
        if (!isnan(pressure)) {
            sensors["pressure"] = pressure;
        }
    }
}

String SensorManager::getSensorList() {
    String sensors = "";
    if (dhtAvailable) {
        sensors += "DHT22";
    }
    if (bmpAvailable) {
        if (sensors.length() > 0) sensors += ",";
        sensors += "BMP280";
    }
    return sensors;
}

String SensorManager::getCapabilities() {
    String capabilities = "";
    if (dhtAvailable) {
        capabilities += "temperature,humidity";
    }
    if (bmpAvailable) {
        if (capabilities.length() > 0) capabilities += ",";
        capabilities += "pressure";
    }
    return capabilities;
}

float SensorManager::getTemperature() {
    if (dhtAvailable) {
        return dht.readTemperature();
    }
    return NAN;
}

float SensorManager::getHumidity() {
    if (dhtAvailable) {
        return dht.readHumidity();
    }
    return NAN;
}

float SensorManager::getPressure() {
    if (bmpAvailable) {
        return bmp.readPressure() / 100.0;
    }
    return NAN;
}

bool SensorManager::isDhtAvailable() {
    return dhtAvailable;
}

bool SensorManager::isBmpAvailable() {
    return bmpAvailable;
}
```

### 11.14 Command Handler (commands/command_handler.h)
```cpp
#ifndef COMMAND_HANDLER_H
#define COMMAND_HANDLER_H

#include <ArduinoJson.h>

class CommandHandler {
public:
    static void processCommand(const char* message);
    static void handleActivateCommand(JsonDocument& doc);
    static void handleRestartCommand(JsonDocument& doc);
    static void handleDeactivateCommand(JsonDocument& doc);
    static void handleConfigureCommand(JsonDocument& doc);
    static void sendCommandResponse(const char* commandType, bool success, const char* message);
};

#endif
```

### 11.15 Command Handler Implementation (commands/command_handler.cpp)
```cpp
#include "command_handler.h"
#include "mqtt/mqtt_client.h"
#include "utils/eeprom_manager.h"

void CommandHandler::processCommand(const char* message) {
    DynamicJsonDocument doc(1024);
    DeserializationError error = deserializeJson(doc, message);
    
    if (error) {
        Serial.println("Failed to parse command");
        return;
    }
    
    if (doc.containsKey("command")) {
        String command = doc["command"].as<String>();
        
        if (command == "ACTIVATE") {
            handleActivateCommand(doc);
        } else if (command == "RESTART") {
            handleRestartCommand(doc);
        } else if (command == "DEACTIVATE") {
            handleDeactivateCommand(doc);
        } else if (command == "CONFIGURE") {
            handleConfigureCommand(doc);
        } else {
            Serial.print("Unknown command: ");
            Serial.println(command);
        }
    }
}

void CommandHandler::handleActivateCommand(JsonDocument& doc) {
    Serial.println("Processing ACTIVATE command");
    
    if (doc.containsKey("config")) {
        JsonObject config = doc["config"];
        
        // Update device configuration
        if (config.containsKey("device_name")) {
            String deviceName = config["device_name"].as<String>();
            EepromManager eepromManager;
            eepromManager.saveDeviceInfo(deviceName, "");
        }
        
        // Update data interval if provided
        if (config.containsKey("data_interval")) {
            int interval = config["data_interval"].as<int>();
            // TODO: Update data interval
        }
        
        // Update alert thresholds if provided
        if (config.containsKey("alert_thresholds")) {
            // TODO: Update alert thresholds
        }
    }
    
    sendCommandResponse("ACTIVATE", true, "Device activated successfully");
}

void CommandHandler::handleRestartCommand(JsonDocument& doc) {
    Serial.println("Processing RESTART command");
    
    sendCommandResponse("RESTART", true, "Device restarting");
    
    delay(1000);
    ESP.restart();
}

void CommandHandler::handleDeactivateCommand(JsonDocument& doc) {
    Serial.println("Processing DEACTIVATE command");
    
    // TODO: Implement deactivation logic
    
    sendCommandResponse("DEACTIVATE", true, "Device deactivated");
}

void CommandHandler::handleConfigureCommand(JsonDocument& doc) {
    Serial.println("Processing CONFIGURE command");
    
    if (doc.containsKey("config")) {
        JsonObject config = doc["config"];
        
        // Update various configuration parameters
        if (config.containsKey("data_interval")) {
            int interval = config["data_interval"].as<int>();
            // TODO: Update data interval
        }
        
        if (config.containsKey("alert_thresholds")) {
            // TODO: Update alert thresholds
        }
    }
    
    sendCommandResponse("CONFIGURE", true, "Configuration updated");
}

void CommandHandler::sendCommandResponse(const char* commandType, bool success, const char* message) {
    DynamicJsonDocument doc(512);
    doc["command"] = commandType;
    doc["status"] = success ? "SUCCESS" : "FAILED";
    doc["message"] = message;
    doc["timestamp"] = millis() / 1000;
    
    String response;
    serializeJson(doc, response);
    
    String topic = "iot/devices/" + deviceId + "/response";
    mqttClient.publish(topic.c_str(), response.c_str());
}
```

### 11.16 EEPROM Manager (utils/eeprom_manager.h)
```cpp
#ifndef EEPROM_MANAGER_H
#define EEPROM_MANAGER_H

#include <EEPROM.h>

class EepromManager {
public:
    void begin();
    bool isWifiConfigured();
    void saveWifiCredentials(const String& ssid, const String& password);
    void saveDeviceInfo(const String& name, const String& description);
    String getWifiSsid();
    String getWifiPassword();
    String getDeviceName();
    String getDeviceDescription();
    void clearAll();
    void printStoredData();
};

#endif
```

### 11.17 EEPROM Manager Implementation (utils/eeprom_manager.cpp)
```cpp
#include "eeprom_manager.h"
#include "config/device_config.h"

void EepromManager::begin() {
    EEPROM.begin(EEPROM_SIZE);
}

bool EepromManager::isWifiConfigured() {
    String ssid = getWifiSsid();
    return !ssid.isEmpty();
}

void EepromManager::saveWifiCredentials(const String& ssid, const String& password) {
    // Save SSID
    for (int i = 0; i < ssid.length(); i++) {
        EEPROM.write(WIFI_SSID_ADDR + i, ssid[i]);
    }
    EEPROM.write(WIFI_SSID_ADDR + ssid.length(), '\0');
    
    // Save password
    for (int i = 0; i < password.length(); i++) {
        EEPROM.write(WIFI_PASSWORD_ADDR + i, password[i]);
    }
    EEPROM.write(WIFI_PASSWORD_ADDR + password.length(), '\0');
    
    EEPROM.commit();
    Serial.println("WiFi credentials saved to EEPROM");
}

void EepromManager::saveDeviceInfo(const String& name, const String& description) {
    // Save device name
    for (int i = 0; i < name.length(); i++) {
        EEPROM.write(DEVICE_NAME_ADDR + i, name[i]);
    }
    EEPROM.write(DEVICE_NAME_ADDR + name.length(), '\0');
    
    // Save device description
    for (int i = 0; i < description.length(); i++) {
        EEPROM.write(DEVICE_DESCRIPTION_ADDR + i, description[i]);
    }
    EEPROM.write(DEVICE_DESCRIPTION_ADDR + description.length(), '\0');
    
    EEPROM.commit();
    Serial.println("Device info saved to EEPROM");
}

String EepromManager::getWifiSsid() {
    String ssid = "";
    int i = 0;
    char c;
    
    while ((c = EEPROM.read(WIFI_SSID_ADDR + i)) != '\0' && i < 32) {
        ssid += c;
        i++;
    }
    
    return ssid;
}

String EepromManager::getWifiPassword() {
    String password = "";
    int i = 0;
    char c;
    
    while ((c = EEPROM.read(WIFI_PASSWORD_ADDR + i)) != '\0' && i < 64) {
        password += c;
        i++;
    }
    
    return password;
}

String EepromManager::getDeviceName() {
    String name = "";
    int i = 0;
    char c;
    
    while ((c = EEPROM.read(DEVICE_NAME_ADDR + i)) != '\0' && i < 64) {
        name += c;
        i++;
    }
    
    return name;
}

String EepromManager::getDeviceDescription() {
    String description = "";
    int i = 0;
    char c;
    
    while ((c = EEPROM.read(DEVICE_DESCRIPTION_ADDR + i)) != '\0' && i < 128) {
        description += c;
        i++;
    }
    
    return description;
}

void EepromManager::clearAll() {
    for (int i = 0; i < EEPROM_SIZE; i++) {
        EEPROM.write(i, 0);
    }
    EEPROM.commit();
    Serial.println("EEPROM cleared");
}

void EepromManager::printStoredData() {
    Serial.println("=== Stored Data ===");
    Serial.print("SSID: ");
    Serial.println(getWifiSsid());
    Serial.print("Password: ");
    Serial.println(getWifiPassword());
    Serial.print("Device Name: ");
    Serial.println(getDeviceName());
    Serial.print("Device Description: ");
    Serial.println(getDeviceDescription());
    Serial.println("==================");
}
```

### 11.18 Build và Upload Instructions

#### 11.18.1 Cài đặt PlatformIO
```bash
# Cài đặt PlatformIO Core
pip install platformio

# Hoặc sử dụng VS Code với PlatformIO extension
```

#### 11.18.2 Build Project
```bash
# Build project
pio run

# Build và upload
pio run --target upload

# Monitor serial output
pio device monitor
```

#### 11.18.3 Cấu hình trước khi upload
1. Cập nhật `config/device_config.h`:
   - Thay đổi `ACTIVE_CODE` thành mã unique cho thiết bị
   - Cập nhật `MQTT_BROKER`, `MQTT_USERNAME`, `MQTT_PASSWORD`
   - Điều chỉnh pin numbers cho sensors

2. Cập nhật `config/wifi_config.h` (nếu cần):
   - Thay đổi AP password
   - Điều chỉnh timeout values

#### 11.18.4 Testing Workflow
1. Upload firmware lên ESP32
2. ESP32 khởi động ở AP mode
3. Kết nối vào WiFi "ESP32_Device_[MAC]"
4. Truy cập http://192.168.4.1
5. Cấu hình WiFi và thông tin thiết bị
6. ESP32 kết nối WiFi và gửi registration message
7. Kiểm tra MQTT messages trên broker
8. Test QR code activation

### 11.19 Troubleshooting

#### 11.19.1 Common Issues
- **WiFi không kết nối**: Kiểm tra credentials và signal strength
- **MQTT không kết nối**: Kiểm tra broker URL và credentials
- **Sensors không đọc được**: Kiểm tra wiring và pin configuration
- **Web server không load**: Kiểm tra SPIFFS và HTML file

#### 11.19.2 Debug Commands
```cpp
// Enable debug output
#define CORE_DEBUG_LEVEL 3

// Print WiFi status
Serial.println(WiFi.status());

// Print MQTT state
Serial.println(client.state());

// Print sensor readings
Serial.print("Temperature: ");
Serial.println(sensorManager.getTemperature());
```

#### 11.19.3 Factory Reset
```cpp
// Add factory reset function
void factoryReset() {
    EepromManager eepromManager;
    eepromManager.clearAll();
    ESP.restart();
}

// Call from setup() if button pressed
if (digitalRead(RESET_BUTTON_PIN) == LOW) {
    factoryReset();
}
``` 
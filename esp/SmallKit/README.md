# ESP32 IoT Device - Hệ thống Quản lý Thiết bị IoT

## Tổng quan

Code ESP32 này được thiết kế theo hệ thống IoT Device Management, hỗ trợ:

- **Cấu hình WiFi qua Access Point Mode**
- **Auto-registration** với Active Code
- **QR Code activation** để kích hoạt thiết bị
- **Thu thập dữ liệu real-time** từ cảm biến
- **MQTT communication** với server

## Cấu hình phần cứng

### Cảm biến được hỗ trợ:

- **DHT22**: Cảm biến nhiệt độ và độ ẩm (chân D4)
- **Gas Sensor**: Cảm biến khí gas (chân A0)
- **LCD I2C**: Màn hình hiển thị (địa chỉ 0x27, chân SDA=D2, SCL=D1)

### Điều khiển:

- **LED**: Đèn LED (chân D5/GPIO14)
- **Buzzer**: Còi cảnh báo (chân D6/GPIO12)

## Cài đặt và sử dụng

### 1. Cấu hình WiFi

1. **Khởi động ESP32**: ESP32 sẽ tạo WiFi network "Gateway_01" với mật khẩu "12345678"
2. **Kết nối WiFi**: Kết nối vào WiFi "Gateway_01" từ điện thoại/máy tính
3. **Truy cập web interface**: Mở trình duyệt và truy cập http://192.168.4.1
4. **Cấu hình thông tin**:
   - WiFi SSID và password của lab
   - Tên thiết bị (friendly name)
   - Mô tả thiết bị
   - MQTT Server address và port
   - MQTT Username và password
5. **Lưu cấu hình**: Nhấn "Save Configuration"

### 2. Auto-Registration

Sau khi kết nối WiFi thành công:

- ESP32 sẽ tự động gửi registration message đến MQTT topic `iot/devices/register`
- Server sẽ phản hồi với device_id và cấu hình
- Thiết bị sẽ có status "REGISTERED" (chưa active)

### 3. QR Code Activation

Để kích hoạt thiết bị:

1. **Tạo QR Code**: Server tạo QR code chứa Active Code "IOT_ACT_123456789"
2. **In và dán**: In QR code và dán lên thiết bị
3. **Quét QR Code**: Admin quét QR code từ web interface
4. **Kích hoạt**: Thiết bị được đổi status thành "ACTIVE"

### 4. Thu thập dữ liệu

Sau khi được kích hoạt:

- ESP32 gửi dữ liệu sensor mỗi 30 giây (có thể cấu hình)
- Dữ liệu được gửi đến topic `iot/devices/{device_id}/data`
- Chỉ thiết bị ACTIVE mới hiển thị trên dashboard

## Cấu hình MQTT

### MQTT Broker (có thể cấu hình qua web interface):

- Server: `14.225.255.177` (mặc định)
- Port: `1883` (mặc định)
- Username: `admin` (mặc định)
- Password: `admin` (mặc định)

**Lưu ý**: Thông tin MQTT broker có thể được cấu hình qua web interface khi setup thiết bị.

### Topics:

#### Registration:

- `iot/devices/register` - ESP32 gửi registration
- `iot/devices/register/response` - Server phản hồi

#### Device-specific:

- `iot/devices/{device_id}/data` - Gửi dữ liệu sensor
- `iot/devices/{device_id}/config` - Nhận cấu hình
- `iot/devices/{device_id}/commands` - Nhận lệnh điều khiển
- `iot/devices/{device_id}/status` - Gửi trạng thái

## Format dữ liệu

### Registration Message:

```json
{
  "mac_address": "AA:BB:CC:DD:EE:FF",
  "device_name": "Temperature Sensor Lab A",
  "active_code": "IOT_ACT_123456789",
  "device_type": "TEMPERATURE_HUMIDITY_SENSOR",
  "firmware_version": "1.0.0",
  "sensors": "DHT22,GAS_SENSOR",
  "capabilities": "temperature,humidity,gas",
  "wifi_ssid": "PTIT_LAB_WIFI"
}
```

### Sensor Data:

```json
{
  "device_id": "IOT_001",
  "timestamp": "2024-01-15T10:30:00Z",
  "sensors": {
    "temperature": 25.5,
    "humidity": 60.2,
    "gas": 150
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

## Commands được hỗ trợ

### ACTIVATE:

```json
{
  "command": "ACTIVATE",
  "config": {
    "device_name": "Temperature Sensor Lab A",
    "data_interval": 30
  }
}
```

### RESTART:

```json
{
  "command": "RESTART"
}
```

### LED_CONTROL:

```json
{
  "command": "LED_CONTROL",
  "led_state": 1
}
```

## Trạng thái thiết bị

- **REGISTERED**: Thiết bị đã đăng ký, chưa kích hoạt
- **ACTIVE**: Thiết bị đã kích hoạt, hiển thị trên dashboard
- **OFFLINE**: Thiết bị không gửi dữ liệu
- **ERROR**: Thiết bị có lỗi

## Hiển thị LCD

### Dòng 1:

- **REGISTERED**: Hiển thị "REGISTER" + "R"
- **ACTIVE**: Hiển thị tên thiết bị (8 ký tự đầu) + "A"

### Dòng 2:

- Hiển thị nhiệt độ và độ ẩm: "T:25.5C H:60.2%"

## Cảnh báo Gas

- Khi giá trị gas vượt ngưỡng (500), buzzer sẽ nháy
- Ngưỡng có thể điều chỉnh trong code

## Troubleshooting

### ESP32 không kết nối WiFi:

1. Kiểm tra SSID và password
2. Nhấn giữ nút setup 10 giây để reset cấu hình
3. Cấu hình lại WiFi

### Không nhận được dữ liệu:

1. Kiểm tra kết nối MQTT
2. Đảm bảo thiết bị đã được kích hoạt (ACTIVE)
3. Kiểm tra device_id đã được gán

### LCD không hiển thị:

1. Kiểm tra kết nối I2C (SDA=D2, SCL=D1)
2. Kiểm tra địa chỉ I2C (mặc định 0x27)

## Thay đổi Active Code

Để thay đổi Active Code cho thiết bị khác:

1. Sửa `#define ACTIVE_CODE "IOT_ACT_123456789"` trong file `SmallKit.ino`
2. Upload lại code
3. Reset cấu hình và cấu hình lại

## Phát triển

### Thêm cảm biến mới:

1. Thêm định nghĩa GPIO trong phần `ĐỊNH NGHĨA GPIO`
2. Thêm hàm đọc dữ liệu cảm biến
3. Cập nhật `sendDataToMQTT()` để gửi dữ liệu mới
4. Cập nhật `DEVICE_CAPABILITIES` và `sensors` trong registration

### Thêm command mới:

1. Thêm case xử lý command trong `callback()`
2. Thêm hàm thực hiện command
3. Cập nhật documentation

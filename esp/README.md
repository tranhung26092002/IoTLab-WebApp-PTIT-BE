# ESP32 IoT Device Firmware

## Hướng dẫn build, upload firmware và nạp file cấu hình

### 1. Cài đặt PlatformIO
- Cài đặt PlatformIO Core:
  ```bash
  pip install platformio
  ```
- Hoặc cài extension PlatformIO IDE cho VS Code.

### 2. Build project
```bash
python -m platformio run
```

### 3. Nạp firmware (code) lên ESP32
```bash
python -m platformio run --target upload
```
- **Lưu ý:** Nếu gặp lỗi "Failed to connect to ESP32: Wrong boot mode detected...", hãy:
  1. Nhấn giữ nút **BOOT** trên ESP32.
  2. Trong khi giữ BOOT, nhấn nút **RESET (EN)** một lần rồi thả ra (vẫn giữ BOOT).
  3. Giữ BOOT cho đến khi quá trình nạp code bắt đầu (thấy dòng "Writing at..." hoặc "Connecting..."), sau đó có thể thả BOOT.

### 4. Nạp file cấu hình (SPIFFS: nạp file html lên flash)
```bash
python -m platformio run --target uploadfs
```
- **Lưu ý:** Khi upload SPIFFS, cũng cần giữ nút **BOOT** như khi nạp code nếu gặp lỗi boot mode.
- Lệnh này sẽ upload toàn bộ thư mục `data/` (ví dụ: `data/config.html`) lên bộ nhớ flash của ESP32.
- Sau khi nạp, ESP32 có thể phục vụ file cấu hình qua web server.

### 5. Monitor serial output
```bash
python -m platformio device monitor
```

### 6. Cấu hình trước khi upload
- Sửa file `src/config/device_config.h`:
  - Thay đổi `ACTIVE_CODE` thành mã unique cho thiết bị
  - Cập nhật `MQTT_BROKER`, `MQTT_USERNAME`, `MQTT_PASSWORD`
  - Điều chỉnh chân kết nối sensors nếu cần

### 7. Thiết lập cấu hình WiFi và MQTT Broker qua giao diện web
- Sau khi upload, ESP32 sẽ khởi động ở chế độ AP nếu chưa cấu hình WiFi.
- Kết nối WiFi "ESP32_Device_[MAC]", truy cập http://192.168.4.1 để cấu hình.
- **Tại giao diện cấu hình web, bạn cần nhập:**
  - Thông tin WiFi: SSID, Password, Tên thiết bị, Mô tả thiết bị
  - Thông tin MQTT Broker: Địa chỉ (host), Port, Username, Password (có thể để trống nếu broker không yêu cầu)
- Nhấn "Save Configuration" để lưu lại cấu hình. Thiết bị sẽ tự động khởi động lại và kết nối theo thông tin bạn đã nhập.

### 8. Reset cấu hình về mặc định
- Khi thiết bị đang chạy, **nhấn giữ nút BOOT (GPIO0) trong 10 giây** để xóa toàn bộ cấu hình (WiFi, MQTT, ...), thiết bị sẽ tự động khởi động lại về chế độ cấu hình ban đầu (AP mode).

## Cấu trúc thư mục
```
esp/
├── platformio.ini
├── README.md
├── src/
│   ├── main.cpp
│   ├── config/
│   │   ├── wifi_config.h
│   │   ├── mqtt_config.h
│   │   └── device_config.h
│   ├── wifi/
│   │   ├── wifi_manager.h
│   │   └── wifi_manager.cpp
│   ├── mqtt/
│   │   ├── mqtt_client.h
│   │   └── mqtt_client.cpp
│   ├── web_server/
│   │   ├── web_server.h
│   │   └── web_server.cpp
│   ├── sensors/
│   │   ├── sensor_manager.h
│   │   └── sensor_manager.cpp
│   ├── commands/
│   │   ├── command_handler.h
│   │   └── command_handler.cpp
│   └── utils/
│       ├── eeprom_manager.h
│       └── eeprom_manager.cpp
├── data/
│   └── config.html
└── ...
```

## Tham khảo chi tiết thiết kế trong file `IoT_Device_Management_System_Design.md` 
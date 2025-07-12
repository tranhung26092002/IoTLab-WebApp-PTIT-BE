// Cấu hình cho nhiều thiết bị IoT
// Mỗi thiết bị cần có Active Code unique

// Thiết bị 1 - Cảm biến nhiệt độ, độ ẩm và khí gas Lab A
#define DEVICE_1_ACTIVE_CODE "IOT_ACT_123456789"
#define DEVICE_1_TYPE "TEMPERATURE_HUMIDITY_GAS_SENSOR"
#define DEVICE_1_NAME "Temp Sensor Lab A"
#define DEVICE_1_DESCRIPTION "Monitor temperature, humidity and gas in Lab A"

// Thiết bị 2 - Cảm biến nhiệt độ, độ ẩm và khí gas Lab B
#define DEVICE_2_ACTIVE_CODE "IOT_ACT_987654321"
#define DEVICE_2_TYPE "TEMPERATURE_HUMIDITY_GAS_SENSOR"
#define DEVICE_2_NAME "Temp Sensor Lab B"
#define DEVICE_2_DESCRIPTION "Monitor temperature, humidity and gas in Lab B"

// Thiết bị 3 - Cảm biến khí gas Lab A
#define DEVICE_3_ACTIVE_CODE "IOT_ACT_456789123"
#define DEVICE_3_TYPE "GAS_SENSOR"
#define DEVICE_3_NAME "Gas Sensor Lab A"
#define DEVICE_3_DESCRIPTION "Monitor gas levels in Lab A"

// Thiết bị 4 - Cảm biến áp suất Lab B
#define DEVICE_4_ACTIVE_CODE "IOT_ACT_789123456"
#define DEVICE_4_TYPE "PRESSURE_SENSOR"
#define DEVICE_4_NAME "Pressure Sensor Lab B"
#define DEVICE_4_DESCRIPTION "Monitor atmospheric pressure in Lab B"

// Thiết bị 5 - Cảm biến ánh sáng Lab C
#define DEVICE_5_ACTIVE_CODE "IOT_ACT_321654987"
#define DEVICE_5_TYPE "LIGHT_SENSOR"
#define DEVICE_5_NAME "Light Sensor Lab C"
#define DEVICE_5_DESCRIPTION "Monitor light levels in Lab C"

// Hướng dẫn sử dụng:
// 1. Chọn một thiết bị từ danh sách trên
// 2. Thay đổi các định nghĩa trong SmallKit.ino:
//    - ACTIVE_CODE
//    - DEVICE_TYPE
//    - DEVICE_NAME (nếu cần)
// 3. Upload code lên ESP32
// 4. Cấu hình WiFi và thông tin thiết bị
// 5. Thiết bị sẽ tự động register với Active Code đã chọn

// Lưu ý: Mỗi Active Code phải unique trong hệ thống
// Không được sử dụng cùng một Active Code cho nhiều thiết bị
#ifndef LED_MANAGER_H
#define LED_MANAGER_H

#include <Arduino.h>
#include "../config/device_config.h"

// Các trạng thái led
enum LedStatus {
    LED_FAST_BLINK,      // Nháy nhanh (chưa cấu hình, đang phát wifi)
    LED_SLOW_BLINK,      // Nháy chậm (đã cấu hình, chưa kết nối wifi)
    LED_CONNECTED_BLINK, // Nháy kiểu khác (đã kết nối wifi)
    LED_DATA_BLINK       // Nháy kiểu khác (gửi data thành công)
};

class LedManager {
private:
    int ledPin;
    LedStatus currentStatus;
    unsigned long lastToggleTime;
    bool ledState;
    unsigned long blinkInterval;
    void updateBlinkInterval();
public:
    LedManager(int pin = STATUS_LED_PIN);
    void begin();
    void setStatus(LedStatus status);
    void handle(); // Gọi trong loop để cập nhật trạng thái nháy
    LedStatus getStatus() const;
};

#endif 
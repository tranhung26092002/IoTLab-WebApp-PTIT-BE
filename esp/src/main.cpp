#include <Arduino.h>
#include "utils/eeprom_manager.h"
// ... các include khác sẽ được thêm sau

#define BOOT_PIN 0
#define BOOT_HOLD_TIME 10000 // 10 giây

unsigned long bootHoldStart = 0;
bool bootWasLow = false;

void checkBootForResetLoop() {
    if (digitalRead(BOOT_PIN) == LOW) {
        if (!bootWasLow) {
            bootHoldStart = millis();
            bootWasLow = true;
        } else if (millis() - bootHoldStart >= BOOT_HOLD_TIME) {
            Serial.println("[RESET] Nút BOOT được giữ 10s. Đang xóa cấu hình...");
            EepromManager eeprom;
            eeprom.begin();
            eeprom.clearAll();
            Serial.println("[RESET] Đã xóa cấu hình. Đang khởi động lại...");
            delay(1000);
            ESP.restart();
        }
    } else {
        bootWasLow = false;
    }
}

void setup() {
    Serial.begin(115200);
    pinMode(BOOT_PIN, INPUT_PULLUP);
    // Khởi tạo các thành phần chính
}

void loop() {
    checkBootForResetLoop();
    // Vòng lặp chính của thiết bị
    delay(10);
} 
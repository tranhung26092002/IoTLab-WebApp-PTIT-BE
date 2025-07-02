#include "led_manager.h"

LedManager::LedManager(int pin) : ledPin(pin), currentStatus(LED_FAST_BLINK), lastToggleTime(0), ledState(false), blinkInterval(100) {}

void LedManager::begin() {
    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, LOW);
    updateBlinkInterval();
}

void LedManager::setStatus(LedStatus status) {
    if (currentStatus != status) {
        currentStatus = status;
        updateBlinkInterval();
        lastToggleTime = millis();
        ledState = false;
        digitalWrite(ledPin, ledState ? HIGH : LOW);
    }
}

void LedManager::updateBlinkInterval() {
    switch (currentStatus) {
        case LED_FAST_BLINK:
            blinkInterval = 100; // 100ms nháy nhanh
            break;
        case LED_SLOW_BLINK:
            blinkInterval = 500; // 500ms nháy chậm
            break;
        case LED_CONNECTED_BLINK:
            blinkInterval = 1000; // 1s nháy (kết nối wifi)
            break;
        case LED_DATA_BLINK:
            blinkInterval = 50; // 50ms nháy rất nhanh khi gửi data
            break;
        default:
            blinkInterval = 500;
            break;
    }
}

void LedManager::handle() {
    unsigned long now = millis();
    if (currentStatus == LED_DATA_BLINK) {
        // Nháy nhanh 2 lần rồi trở về trạng thái trước đó
        static int blinkCount = 0;
        if (now - lastToggleTime >= blinkInterval) {
            ledState = !ledState;
            digitalWrite(ledPin, ledState ? HIGH : LOW);
            lastToggleTime = now;
            blinkCount++;
            if (blinkCount >= 4) { // 2 chu kỳ bật/tắt
                setStatus(LED_CONNECTED_BLINK); // Quay lại trạng thái kết nối wifi
                blinkCount = 0;
            }
        }
    } else {
        if (now - lastToggleTime >= blinkInterval) {
            ledState = !ledState;
            digitalWrite(ledPin, ledState ? HIGH : LOW);
            lastToggleTime = now;
        }
    }
}

LedStatus LedManager::getStatus() const {
    return currentStatus;
} 
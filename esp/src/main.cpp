#include <Arduino.h>
#include "commands/command_handler.h"
#include "sensors/sensor_manager.h"
#include "mqtt/mqtt_client.h"
#include "utils/eeprom_manager.h"
#include "utils/led_manager.h"
#include "web_server/web_server.h"
#include "wifi/wifi_manager.h"
#include "config/device_config.h"
#include "config/mqtt_config.h"
#include "config/wifi_config.h"
// ... các include khác sẽ được thêm sau

#define BOOT_PIN 0
#define BOOT_HOLD_TIME 10000 // 10 giây

unsigned long bootHoldStart = 0;
bool bootWasLow = false;

LedManager ledManager;
EepromManager eeprom;
WiFiManager wifiManager;
WebServer webServer;
SensorManager sensorManager;
MqttClient mqttClient;

void printCurrentStatus() {
    eeprom.begin();
    bool wifiConfigured = eeprom.isWifiConfigured();
    Serial.println("============================");
    Serial.println("THÔNG TIN TRẠNG THÁI HỆ THỐNG");
    if (!wifiConfigured) {
        Serial.println("Chế độ: PHÁT WIFI (AP MODE) - Chưa cấu hình");
        Serial.print("LED: "); Serial.println("NHÁY NHANH (LED_FAST_BLINK)");
    } else {
        Serial.println("Chế độ: ĐANG KẾT NỐI WIFI (STA MODE)");
        Serial.print("SSID: "); Serial.println(eeprom.getWifiSsid());
        Serial.print("Password: "); Serial.println(eeprom.getWifiPassword());
        Serial.print("Tên thiết bị: "); Serial.println(eeprom.getDeviceName());
        Serial.print("Mô tả: "); Serial.println(eeprom.getDeviceDescription());
        Serial.print("MQTT Broker: "); Serial.println(eeprom.getMqttBroker());
        Serial.print("MQTT Port: "); Serial.println(eeprom.getMqttPort());
        Serial.print("MQTT User: "); Serial.println(eeprom.getMqttUsername());
        Serial.print("LED: "); Serial.println("NHÁY CHẬM hoặc ĐÃ KẾT NỐI WIFI");
    }
    Serial.println("============================");
}

void checkBootForResetLoop() {
    static bool resetTriggered = false;
    static LedStatus prevLedStatus = LED_FAST_BLINK;
    if (digitalRead(BOOT_PIN) == LOW) {
        if (!bootWasLow) {
            bootHoldStart = millis();
            bootWasLow = true;
            resetTriggered = false;
            // Lưu trạng thái led trước khi nháy đặc biệt
            prevLedStatus = ledManager.getStatus();
            ledManager.setStatus(LED_DATA_BLINK); // Nháy siêu nhanh khi giữ nút
        } else if (!resetTriggered && millis() - bootHoldStart >= BOOT_HOLD_TIME) {
            Serial.println("[RESET] Nút BOOT được giữ 10s. Đang xóa cấu hình...");
            EepromManager eeprom;
            eeprom.begin();
            eeprom.clearAll();
            Serial.println("[RESET] Đã xóa cấu hình. Đang khởi động lại...");
            resetTriggered = true;
            delay(1000);
            ESP.restart();
        } else if (!resetTriggered) {
            // Log thời gian giữ nút (mỗi 2s)
            static unsigned long lastLog = 0;
            if (millis() - lastLog > 2000) {
                Serial.print("[RESET] Đang giữ nút BOOT: ");
                Serial.print((millis() - bootHoldStart) / 1000);
                Serial.println(" giây");
                lastLog = millis();
            }
        }
    } else {
        if (bootWasLow) {
            // Trả lại trạng thái led trước đó khi nhả nút
            ledManager.setStatus(prevLedStatus);
        }
        bootWasLow = false;
        resetTriggered = false;
    }
}

void mqttMessageCallback(char* topic, byte* payload, unsigned int length) {
    // Chuyển payload thành chuỗi và xử lý lệnh
    String msg;
    for (unsigned int i = 0; i < length; i++) msg += (char)payload[i];
    CommandHandler::processCommand(msg.c_str());
}

void setup() {
    Serial.begin(115200);
    Serial.println("ESP32 STARTED - OK");
    pinMode(BOOT_PIN, INPUT_PULLUP);
    ledManager.begin();
    sensorManager.begin();
    mqttClient.begin();
    // Giả lập trạng thái ban đầu: chưa cấu hình -> nháy nhanh
    ledManager.setStatus(LED_FAST_BLINK);
    printCurrentStatus();
    if (!eeprom.isWifiConfigured()) {
        wifiManager.startAccessPoint();
        webServer.begin();
        webServer.startConfigurationMode();
    } else {
        // Đã cấu hình wifi, thử kết nối wifi và MQTT
        if (wifiManager.connectToWifi()) {
            if (mqttClient.connect()) {
                mqttClient.setCallback(mqttMessageCallback);
                // Đăng ký topic nhận lệnh nếu cần
                // mqttClient.subscribe("iot/device/command");
            }
        }
    }
}

unsigned long lastSensorSend = 0;
void loop() {
    checkBootForResetLoop();
    ledManager.handle();
    mqttClient.loop();
    // Định kỳ đọc sensor và gửi lên MQTT nếu đã kết nối
    if (mqttClient.isConnected() && millis() - lastSensorSend > DATA_SEND_INTERVAL) {
        StaticJsonDocument<256> doc;
        JsonObject sensors = doc.createNestedObject("sensors");
        sensorManager.readSensors(sensors);
        char payload[256];
        serializeJson(doc, payload);
        mqttClient.publish("iot/device/data", payload);
        lastSensorSend = millis();
    }
    delay(10);
} 
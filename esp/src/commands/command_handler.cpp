#include "command_handler.h"
#include <ArduinoJson.h>
#include <Arduino.h>
#include "../mqtt/mqtt_client.h"

extern MqttClient mqttClient;

void CommandHandler::processCommand(const char* message) {
    StaticJsonDocument<256> doc;
    DeserializationError err = deserializeJson(doc, message);
    if (err) {
        Serial.println("[CMD] Lỗi parse JSON command!");
        return;
    }
    String type = doc["type"] | "";
    if (type == "ACTIVATE") {
        handleActivateCommand(doc);
    } else if (type == "RESTART") {
        handleRestartCommand(doc);
    } else if (type == "DEACTIVATE") {
        handleDeactivateCommand(doc);
    } else if (type == "CONFIGURE") {
        handleConfigureCommand(doc);
    } else {
        Serial.println("[CMD] Lệnh không hợp lệ!");
    }
}

void CommandHandler::handleActivateCommand(JsonDocument& doc) {
    // Ví dụ: Kích hoạt thiết bị
    Serial.println("[CMD] Nhận lệnh ACTIVATE");
    sendCommandResponse("ACTIVATE", true, "Thiết bị đã được kích hoạt");
}

void CommandHandler::handleRestartCommand(JsonDocument& doc) {
    Serial.println("[CMD] Nhận lệnh RESTART");
    sendCommandResponse("RESTART", true, "Thiết bị sẽ khởi động lại");
    delay(1000);
    ESP.restart();
}

void CommandHandler::handleDeactivateCommand(JsonDocument& doc) {
    Serial.println("[CMD] Nhận lệnh DEACTIVATE");
    sendCommandResponse("DEACTIVATE", true, "Thiết bị đã bị hủy kích hoạt");
}

void CommandHandler::handleConfigureCommand(JsonDocument& doc) {
    Serial.println("[CMD] Nhận lệnh CONFIGURE");
    // Có thể cập nhật cấu hình từ doc nếu muốn
    sendCommandResponse("CONFIGURE", true, "Cấu hình đã được cập nhật");
}

void CommandHandler::sendCommandResponse(const char* commandType, bool success, const char* message) {
    StaticJsonDocument<128> doc;
    doc["type"] = String(commandType) + "_RESPONSE";
    doc["success"] = success;
    doc["message"] = message;
    char buf[128];
    serializeJson(doc, buf);
    mqttClient.publish("iot/device/response", buf);
}
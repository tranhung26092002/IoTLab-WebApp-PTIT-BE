#include "web_server.h"
#include "../config/device_config.h"
#include "../utils/eeprom_manager.h"

WebServer::WebServer() : server(WEB_SERVER_PORT) {}

void WebServer::begin() {
    // Khởi động web server
}

void WebServer::startConfigurationMode() {
    isConfigurationMode = true;
}

void WebServer::stopConfigurationMode() {
    isConfigurationMode = false;
}

void WebServer::handleRoot(AsyncWebServerRequest *request) {
    // Xử lý root
}

void WebServer::handleConfigure(AsyncWebServerRequest *request) {
    // Xử lý cấu hình
}

void WebServer::handleSaveConfig(AsyncWebServerRequest *request) {
    String ssid, password, deviceName, deviceDescription;
    String mqttBroker, mqttPortStr, mqttUsername, mqttPassword;
    uint16_t mqttPort = 1883;

    if (request->hasParam("ssid", true))
        ssid = request->getParam("ssid", true)->value();
    if (request->hasParam("password", true))
        password = request->getParam("password", true)->value();
    if (request->hasParam("device_name", true))
        deviceName = request->getParam("device_name", true)->value();
    if (request->hasParam("device_description", true))
        deviceDescription = request->getParam("device_description", true)->value();
    if (request->hasParam("mqtt_broker", true))
        mqttBroker = request->getParam("mqtt_broker", true)->value();
    if (request->hasParam("mqtt_port", true))
        mqttPortStr = request->getParam("mqtt_port", true)->value();
    if (request->hasParam("mqtt_username", true))
        mqttUsername = request->getParam("mqtt_username", true)->value();
    if (request->hasParam("mqtt_password", true))
        mqttPassword = request->getParam("mqtt_password", true)->value();

    if (mqttPortStr.length() > 0) {
        mqttPort = mqttPortStr.toInt();
        if (mqttPort == 0) mqttPort = 1883;
    }

    EepromManager eeprom;
    eeprom.begin();
    eeprom.saveWifiCredentials(ssid, password);
    eeprom.saveDeviceInfo(deviceName, deviceDescription);
    eeprom.saveMqttConfig(mqttBroker, mqttPort, mqttUsername, mqttPassword);

    request->send(200, "text/plain", "Configuration saved successfully. Device will restart.");
    delay(1000);
    ESP.restart();
}

void WebServer::handleSaveMqttConfig(AsyncWebServerRequest *request) {
    // Lưu thông tin MQTT broker từ request
} 
#include "web_server.h"
#include "../config/device_config.h"
#include "../utils/eeprom_manager.h"
#include "../utils/led_manager.h"

extern LedManager ledManager;

WebServer::WebServer() : server(WEB_SERVER_PORT) {}

void WebServer::begin() {
    if (!SPIFFS.begin(true)) {
        Serial.println("Lỗi mount SPIFFS!");
        return;
    }
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
        request->send(SPIFFS, "/config.html", "text/html");
    });
    server.on("/device-info", HTTP_GET, [](AsyncWebServerRequest *request){
        EepromManager eeprom;
        eeprom.begin();
        String mac = WiFi.macAddress();
        String ssid = String(AP_SSID_PREFIX) + mac.substring(mac.length() - 6);
        String json = "{";
        json += "\"mac\":\"" + mac + "\",";
        json += "\"deviceType\":\"" + String(DEVICE_TYPE) + "\",";
        json += "\"firmware\":\"" + String(FIRMWARE_VERSION) + "\",";
        json += "\"activeCode\":\"" + String(ACTIVE_CODE) + "\",";
        json += "\"deviceName\":\"" + eeprom.getDeviceName() + "\",";
        json += "\"deviceDescription\":\"" + eeprom.getDeviceDescription() + "\",";
        json += "\"ssid\":\"" + eeprom.getWifiSsid() + "\",";
        json += "\"mqttBroker\":\"" + eeprom.getMqttBroker() + "\",";
        json += "\"mqttPort\":\"" + String(eeprom.getMqttPort()) + "\",";
        json += "\"mqttUsername\":\"" + eeprom.getMqttUsername() + "\",";
        json += "\"mqttPassword\":\"" + eeprom.getMqttPassword() + "\"";
        json += "}";
        request->send(200, "application/json", json);
    });
    server.on("/save-config", HTTP_POST, [this](AsyncWebServerRequest *request){
        this->handleSaveConfig(request);
    });
    server.begin();
    Serial.println("Web server đã khởi động!");
}

void WebServer::startConfigurationMode() {
    isConfigurationMode = true;
    ledManager.setStatus(LED_FAST_BLINK);
}

void WebServer::stopConfigurationMode() {
    isConfigurationMode = false;
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
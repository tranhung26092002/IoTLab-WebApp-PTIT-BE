#include "eeprom_manager.h"
#include "../config/device_config.h"

void EepromManager::begin() {
    EEPROM.begin(EEPROM_SIZE);
}

bool EepromManager::isWifiConfigured() {
    return false;
}

void EepromManager::saveWifiCredentials(const String& ssid, const String& password) {
    // Lưu thông tin WiFi
}

void EepromManager::saveDeviceInfo(const String& name, const String& description) {
    // Lưu thông tin thiết bị
}

String EepromManager::getWifiSsid() {
    return "";
}

String EepromManager::getWifiPassword() {
    return "";
}

String EepromManager::getDeviceName() {
    return "";
}

String EepromManager::getDeviceDescription() {
    return "";
}

void EepromManager::clearAll() {
    for (int i = 0; i < EEPROM_SIZE; ++i) {
        EEPROM.write(i, 0xFF);
    }
    EEPROM.commit();
}

void EepromManager::printStoredData() {
    // In thông tin đã lưu
}

void EepromManager::saveMqttConfig(const String& broker, uint16_t port, const String& username, const String& password) {
    // Lưu thông tin MQTT broker vào EEPROM
}

String EepromManager::getMqttBroker() {
    return "";
}

uint16_t EepromManager::getMqttPort() {
    return 1883;
}

String EepromManager::getMqttUsername() {
    return "";
}

String EepromManager::getMqttPassword() {
    return "";
} 
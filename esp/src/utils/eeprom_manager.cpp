#include "eeprom_manager.h"
#include "../config/device_config.h"

void EepromManager::begin() {
    EEPROM.begin(EEPROM_SIZE);
}

bool EepromManager::isWifiConfigured() {
    char ssid[32];
    for (int i = 0; i < 32; ++i) ssid[i] = EEPROM.read(WIFI_SSID_ADDR + i);
    ssid[31] = '\0';
    String s = String(ssid);
    s.trim();
    return s.length() > 0 && s != String(0xFF, 32);
}

void EepromManager::saveWifiCredentials(const String& ssid, const String& password) {
    for (int i = 0; i < 32; ++i) {
        EEPROM.write(WIFI_SSID_ADDR + i, i < ssid.length() ? ssid[i] : 0);
        EEPROM.write(WIFI_PASSWORD_ADDR + i, i < password.length() ? password[i] : 0);
    }
    EEPROM.commit();
}

void EepromManager::saveDeviceInfo(const String& name, const String& description) {
    for (int i = 0; i < 64; ++i) {
        EEPROM.write(DEVICE_NAME_ADDR + i, i < name.length() ? name[i] : 0);
    }
    for (int i = 0; i < 64; ++i) {
        EEPROM.write(DEVICE_DESCRIPTION_ADDR + i, i < description.length() ? description[i] : 0);
    }
    EEPROM.commit();
}

String EepromManager::getWifiSsid() {
    char ssid[33];
    for (int i = 0; i < 32; ++i) ssid[i] = EEPROM.read(WIFI_SSID_ADDR + i);
    ssid[32] = '\0';
    return String(ssid);
}

String EepromManager::getWifiPassword() {
    char pass[33];
    for (int i = 0; i < 32; ++i) pass[i] = EEPROM.read(WIFI_PASSWORD_ADDR + i);
    pass[32] = '\0';
    return String(pass);
}

String EepromManager::getDeviceName() {
    char name[65];
    for (int i = 0; i < 64; ++i) name[i] = EEPROM.read(DEVICE_NAME_ADDR + i);
    name[64] = '\0';
    return String(name);
}

String EepromManager::getDeviceDescription() {
    char desc[65];
    for (int i = 0; i < 64; ++i) desc[i] = EEPROM.read(DEVICE_DESCRIPTION_ADDR + i);
    desc[64] = '\0';
    return String(desc);
}

void EepromManager::clearAll() {
    for (int i = 0; i < EEPROM_SIZE; ++i) {
        EEPROM.write(i, 0xFF);
    }
    EEPROM.commit();
}

void EepromManager::printStoredData() {
    Serial.println("[EEPROM] SSID: " + getWifiSsid());
    Serial.println("[EEPROM] PASS: " + getWifiPassword());
    Serial.println("[EEPROM] NAME: " + getDeviceName());
    Serial.println("[EEPROM] DESC: " + getDeviceDescription());
    Serial.println("[EEPROM] MQTT BROKER: " + getMqttBroker());
    Serial.println("[EEPROM] MQTT PORT: " + String(getMqttPort()));
    Serial.println("[EEPROM] MQTT USER: " + getMqttUsername());
    Serial.println("[EEPROM] MQTT PASS: " + getMqttPassword());
}

void EepromManager::saveMqttConfig(const String& broker, uint16_t port, const String& username, const String& password) {
    for (int i = 0; i < 64; ++i) {
        EEPROM.write(MQTT_BROKER_ADDR + i, i < broker.length() ? broker[i] : 0);
    }
    EEPROM.write(MQTT_PORT_ADDR, (port >> 8) & 0xFF);
    EEPROM.write(MQTT_PORT_ADDR + 1, port & 0xFF);
    for (int i = 0; i < 32; ++i) {
        EEPROM.write(MQTT_USERNAME_ADDR + i, i < username.length() ? username[i] : 0);
        EEPROM.write(MQTT_PASSWORD_ADDR + i, i < password.length() ? password[i] : 0);
    }
    EEPROM.commit();
}

String EepromManager::getMqttBroker() {
    char broker[65];
    for (int i = 0; i < 64; ++i) broker[i] = EEPROM.read(MQTT_BROKER_ADDR + i);
    broker[64] = '\0';
    return String(broker);
}

uint16_t EepromManager::getMqttPort() {
    uint16_t port = (EEPROM.read(MQTT_PORT_ADDR) << 8) | EEPROM.read(MQTT_PORT_ADDR + 1);
    return port == 0xFFFF ? 1883 : port;
}

String EepromManager::getMqttUsername() {
    char user[33];
    for (int i = 0; i < 32; ++i) user[i] = EEPROM.read(MQTT_USERNAME_ADDR + i);
    user[32] = '\0';
    return String(user);
}

String EepromManager::getMqttPassword() {
    char pass[33];
    for (int i = 0; i < 32; ++i) pass[i] = EEPROM.read(MQTT_PASSWORD_ADDR + i);
    pass[32] = '\0';
    return String(pass);
} 
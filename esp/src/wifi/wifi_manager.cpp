#include "wifi_manager.h"
#include "../config/device_config.h"
#include "../utils/led_manager.h"
#include "../utils/eeprom_manager.h"

extern LedManager ledManager;

WiFiManager::WiFiManager() {
    // Khởi tạo cấu hình WiFi
}

bool WiFiManager::connectToWifi() {
    EepromManager eeprom;
    eeprom.begin();
    String ssid = eeprom.getWifiSsid();
    String pass = eeprom.getWifiPassword();
    if (ssid.length() == 0) return false;
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid.c_str(), pass.c_str());
    Serial.print("Đang kết nối WiFi: ");
    Serial.println(ssid);
    unsigned long start = millis();
    while (WiFi.status() != WL_CONNECTED && millis() - start < WIFI_TIMEOUT) {
        delay(500);
        Serial.print(".");
    }
    bool connected = WiFi.status() == WL_CONNECTED;
    if (connected) {
        Serial.println("\nKết nối WiFi thành công!");
        ledManager.setStatus(LED_CONNECTED_BLINK);
    } else {
        Serial.println("\nKết nối WiFi thất bại!");
        ledManager.setStatus(LED_SLOW_BLINK);
    }
    return connected;
}

void WiFiManager::startAccessPoint() {
    // Bắt đầu chế độ AP
    WiFi.mode(WIFI_AP);
    String mac = WiFi.macAddress();
    mac.replace(":", "");
    String ssid = String(AP_SSID_PREFIX) + mac.substring(mac.length() - 6);
    WiFi.softAP(ssid.c_str(), AP_PASSWORD);
    Serial.print("Đang phát WiFi cấu hình: ");
    Serial.print(ssid);
    Serial.print(" | Password: ");
    Serial.println(AP_PASSWORD);
    isApMode = true;
}

bool WiFiManager::reconnect() {
    WiFi.disconnect();
    delay(1000);
    return connectToWifi();
}

bool WiFiManager::isConnected() {
    return WiFi.status() == WL_CONNECTED;
}

String WiFiManager::getMacAddress() {
    return WiFi.macAddress();
}

int WiFiManager::getSignalStrength() {
    return WiFi.RSSI();
}

void WiFiManager::disconnect() {
    WiFi.disconnect();
} 
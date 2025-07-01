#ifndef EEPROM_MANAGER_H
#define EEPROM_MANAGER_H

#include <EEPROM.h>

class EepromManager {
public:
    void begin();
    bool isWifiConfigured();
    void saveWifiCredentials(const String& ssid, const String& password);
    void saveDeviceInfo(const String& name, const String& description);
    String getWifiSsid();
    String getWifiPassword();
    String getDeviceName();
    String getDeviceDescription();
    void clearAll();
    void printStoredData();
    void saveMqttConfig(const String& broker, uint16_t port, const String& username, const String& password);
    String getMqttBroker();
    uint16_t getMqttPort();
    String getMqttUsername();
    String getMqttPassword();
};

#endif 
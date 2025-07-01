#ifndef WIFI_MANAGER_H
#define WIFI_MANAGER_H

#include <WiFi.h>
// Có thể thêm WiFiManager nếu dùng thư viện ngoài

class WiFiManager {
private:
    bool isApMode = false;
public:
    WiFiManager();
    bool connectToWifi();
    void startAccessPoint();
    bool reconnect();
    bool isConnected();
    String getMacAddress();
    int getSignalStrength();
    void disconnect();
};

#endif 
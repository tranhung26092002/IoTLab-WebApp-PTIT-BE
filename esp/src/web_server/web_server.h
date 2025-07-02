#ifndef WEB_SERVER_H
#define WEB_SERVER_H

#include <ESPAsyncWebServer.h>
#include <SPIFFS.h>

class WebServer {
private:
    AsyncWebServer server;
    bool isConfigurationMode = false;
public:
    WebServer();
    void begin();
    void startConfigurationMode();
    void stopConfigurationMode();
    void handleSaveConfig(AsyncWebServerRequest *request);
};

#endif 
#ifndef MQTT_CLIENT_H
#define MQTT_CLIENT_H

#include <PubSubClient.h>
#include <WiFi.h>

class MqttClient {
private:
    PubSubClient client;
    String clientId;
    bool isMqttConnected = false;
    static void messageCallback(char* topic, byte* payload, unsigned int length);
public:
    MqttClient();
    void begin();
    bool connect();
    void disconnect();
    bool publish(const char* topic, const char* message);
    bool subscribe(const char* topic);
    void loop();
    bool isConnected();
    void setCallback(void (*callback)(char*, byte*, unsigned int));
};

#endif 
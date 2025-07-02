#include "mqtt_client.h"
#include "../config/mqtt_config.h"
#include "../config/device_config.h"
#include "../utils/led_manager.h"
#include "../utils/eeprom_manager.h"
#include "../commands/command_handler.h"
#include <WiFiClient.h>

extern LedManager ledManager;

WiFiClient espClient;

MqttClient::MqttClient() : client(espClient) {}

void MqttClient::begin() {
    // Không cần gì thêm nếu đã khởi tạo client với espClient
}

bool MqttClient::connect() {
    EepromManager eeprom;
    eeprom.begin();
    String broker = eeprom.getMqttBroker();
    uint16_t port = eeprom.getMqttPort();
    String user = eeprom.getMqttUsername();
    String pass = eeprom.getMqttPassword();
    String clientId = String(MQTT_CLIENT_ID_PREFIX) + String(random(0xffff), HEX);

    client.setServer(broker.c_str(), port);

    Serial.print("Đang kết nối MQTT: ");
    Serial.print(broker); Serial.print(":"); Serial.println(port);

    bool connected = false;
    if (user.length() > 0) {
        connected = client.connect(clientId.c_str(), user.c_str(), pass.c_str());
    } else {
        connected = client.connect(clientId.c_str());
    }

    if (connected) {
        Serial.println("Kết nối MQTT thành công!");
        isMqttConnected = true;
    } else {
        Serial.println("Kết nối MQTT thất bại!");
        isMqttConnected = false;
    }
    return connected;
}

void MqttClient::disconnect() {
    client.disconnect();
    isMqttConnected = false;
}

bool MqttClient::publish(const char* topic, const char* message) {
    bool result = client.publish(topic, message);
    if (result) {
        ledManager.setStatus(LED_DATA_BLINK);
    }
    return result;
}

bool MqttClient::subscribe(const char* topic) {
    return client.subscribe(topic);
}

void MqttClient::loop() {
    client.loop();
}

bool MqttClient::isConnected() {
    return client.connected();
}

void MqttClient::setCallback(void (*callback)(char*, byte*, unsigned int)) {
    client.setCallback(callback);
}

void MqttClient::messageCallback(char* topic, byte* payload, unsigned int length) {
    // Chuyển payload thành chuỗi và chuyển tiếp cho CommandHandler
    String msg;
    for (unsigned int i = 0; i < length; i++) msg += (char)payload[i];
    CommandHandler::processCommand(msg.c_str());
} 
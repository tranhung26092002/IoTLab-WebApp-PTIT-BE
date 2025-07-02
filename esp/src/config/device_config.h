#ifndef DEVICE_CONFIG_H
#define DEVICE_CONFIG_H

// Device Information
#define DEVICE_TYPE "TEMPERATURE_HUMIDITY_SENSOR"
#define FIRMWARE_VERSION "1.0.0"
#define ACTIVE_CODE "IOT_ACT_123456789"

// Timing Configuration
#define DATA_SEND_INTERVAL 30000    // 30 seconds
#define HEARTBEAT_INTERVAL 60000    // 60 seconds
#define WIFI_TIMEOUT 30000          // 30 seconds
#define MQTT_TIMEOUT 10000          // 10 seconds

// WiFi Configuration
#define AP_SSID_PREFIX "ESP32_Device_"
#define AP_PASSWORD "12345678"
#define AP_IP "192.168.4.1"
#define AP_GATEWAY "192.168.4.1"
#define AP_SUBNET "255.255.255.0"

// MQTT Configuration
#define MQTT_BROKER "14.225.255.177"
#define MQTT_PORT 1883
#define MQTT_USERNAME "admin"
#define MQTT_PASSWORD "admin"
#define MQTT_CLIENT_ID_PREFIX "ESP32_"
#define MQTT_BROKER_ADDR 192
#define MQTT_PORT_ADDR 256
#define MQTT_USERNAME_ADDR 260
#define MQTT_PASSWORD_ADDR 292

// EEPROM Configuration
#define EEPROM_SIZE 512
#define WIFI_SSID_ADDR 0
#define WIFI_PASSWORD_ADDR 32
#define DEVICE_NAME_ADDR 64
#define DEVICE_DESCRIPTION_ADDR 128

// Sensor Configuration
#define DHT_PIN 4
#define DHT_TYPE DHT22
#define BMP280_SDA_PIN 21
#define BMP280_SCL_PIN 22

// Web Server Configuration
#define WEB_SERVER_PORT 80

// LED Status Pin
#define STATUS_LED_PIN 2  // Chân GPIO2 thường có sẵn led trên board ESP32

#endif 
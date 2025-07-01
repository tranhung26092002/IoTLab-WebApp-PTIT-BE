#include "sensor_manager.h"
#include "../config/device_config.h"

SensorManager::SensorManager() : dht(DHT_PIN, DHT_TYPE) {}

void SensorManager::begin() {
    // Khởi tạo sensors
}

void SensorManager::readSensors(JsonObject& sensors) {
    // Đọc dữ liệu sensors
}

String SensorManager::getSensorList() {
    return "";
}

String SensorManager::getCapabilities() {
    return "";
}

float SensorManager::getTemperature() {
    return 0;
}

float SensorManager::getHumidity() {
    return 0;
}

float SensorManager::getPressure() {
    return 0;
}

bool SensorManager::isDhtAvailable() {
    return dhtAvailable;
}

bool SensorManager::isBmpAvailable() {
    return bmpAvailable;
} 
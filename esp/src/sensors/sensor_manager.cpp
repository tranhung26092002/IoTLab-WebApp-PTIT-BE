#include "sensor_manager.h"
#include "../config/device_config.h"

SensorManager::SensorManager() : dht(DHT_PIN, DHT_TYPE) {}

void SensorManager::begin() {
    dht.begin();
    dhtAvailable = true; // Có thể kiểm tra thực tế nếu cần
    bmpAvailable = bmp.begin(0x76); // Địa chỉ I2C mặc định của BMP280 là 0x76 hoặc 0x77
}

void SensorManager::readSensors(JsonObject& sensors) {
    if (dhtAvailable) {
        float temp = dht.readTemperature();
        float hum = dht.readHumidity();
        if (!isnan(temp)) sensors["temperature"] = temp;
        if (!isnan(hum)) sensors["humidity"] = hum;
    }
    if (bmpAvailable) {
        float pressure = bmp.readPressure() / 100.0F; // hPa
        float temp2 = bmp.readTemperature();
        if (!isnan(pressure)) sensors["pressure"] = pressure;
        if (!isnan(temp2)) sensors["bmp_temperature"] = temp2;
    }
}

String SensorManager::getSensorList() {
    String list = "";
    if (dhtAvailable) list += "DHT22 ";
    if (bmpAvailable) list += "BMP280";
    return list;
}

String SensorManager::getCapabilities() {
    String caps = "";
    if (dhtAvailable) caps += "temperature,humidity;";
    if (bmpAvailable) caps += "pressure,bmp_temperature;";
    return caps;
}

float SensorManager::getTemperature() {
    if (dhtAvailable) {
        float t = dht.readTemperature();
        if (!isnan(t)) return t;
    }
    if (bmpAvailable) {
        float t = bmp.readTemperature();
        if (!isnan(t)) return t;
    }
    return 0;
}

float SensorManager::getHumidity() {
    if (dhtAvailable) {
        float h = dht.readHumidity();
        if (!isnan(h)) return h;
    }
    return 0;
}

float SensorManager::getPressure() {
    if (bmpAvailable) {
        float p = bmp.readPressure() / 100.0F;
        if (!isnan(p)) return p;
    }
    return 0;
}

bool SensorManager::isDhtAvailable() {
    return dhtAvailable;
}

bool SensorManager::isBmpAvailable() {
    return bmpAvailable;
} 
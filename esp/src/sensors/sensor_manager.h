#ifndef SENSOR_MANAGER_H
#define SENSOR_MANAGER_H

#include <DHT.h>
#include <Adafruit_BMP280.h>
#include <ArduinoJson.h>

class SensorManager {
private:
    DHT dht;
    Adafruit_BMP280 bmp;
    bool dhtAvailable = false;
    bool bmpAvailable = false;
public:
    SensorManager();
    void begin();
    void readSensors(JsonObject& sensors);
    String getSensorList();
    String getCapabilities();
    float getTemperature();
    float getHumidity();
    float getPressure();
    bool isDhtAvailable();
    bool isBmpAvailable();
};

#endif 
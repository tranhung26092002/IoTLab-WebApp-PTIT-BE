#define DEBUG

#include "espConfig.h"    // Thư viện cấu hình Wi-Fi và ESP
#include <PubSubClient.h> // Thư viện MQTT
#include <ArduinoJson.h>  // Thư viện xử lý JSON
#include <DHT.h>
#include <Wire.h> // Thư viện giao tiếp I2C

//-------------------- CẤU HÌNH THIẾT BỊ IoT --------------------//

// Active Code unique cho thiết bị (được lưu trong EEPROM)
#define ACTIVE_CODE "IOT_ACT_123456789"
#define DEVICE_TYPE "TEMPERATURE_HUMIDITY_SENSOR"
#define FIRMWARE_VERSION "1.0.0"
#define DEVICE_CAPABILITIES "temperature,humidity,gas"

// Cấu hình MQTT Broker (sẽ được cấu hình qua web interface)
const char *mqtt_server_default = "14.225.255.177";
const char *mqtt_username_default = "admin";
const char *mqtt_password_default = "admin";
const int mqtt_port_default = 1883;

// Thông tin thiết bị (sẽ được cập nhật sau khi registration)
String device_id = "";   // Sẽ được server gán sau khi register
String device_name = ""; // Sẽ được cấu hình qua web interface
String device_description = "";

// Khai báo MQTT client
WiFiClient espClient;
PubSubClient client(espClient);

//-------------------- ĐỊNH NGHĨA GPIO --------------------//

// Cảm biến DHT (ESP32 dùng số GPIO thật)
#define DHTPIN 4      // GPIO4 cho DHT22
#define DHTTYPE DHT22 // Sử dụng DHT22
DHT dht(DHTPIN, DHTTYPE);

// Cảm biến khí gas (ESP32 analog: GPIO36 là VP, hoặc chọn GPIO32, 33, 34, 35, 39)
#define GAS_SENSOR_PIN 36 // GPIO36 (VP) cho cảm biến gas

// Điều khiển LED và Buzzer
#define LED_PIN 14    // GPIO14 cho LED
#define BUZZER_PIN 12 // GPIO12 cho Buzzer

// Ngưỡng cảnh báo khí gas
const int GAS_THRESHOLD = 500;
const long intervalBuzzer = 500;

// Cấu hình thiết bị
int data_interval = 30; // Gửi dữ liệu mỗi 30 giây
bool device_activated = false;
unsigned long last_registration_attempt = 0;
const unsigned long REGISTRATION_RETRY_INTERVAL = 30000; // 30 giây

//-------------------- HÀM XỬ LÝ CẢM BIẾN --------------------//

void getDHT(float &temperature, float &humidity)
{
  temperature = dht.readTemperature();
  humidity = dht.readHumidity();

  if (isnan(temperature) || isnan(humidity))
  {
    Serial.println("Lỗi đọc dữ liệu từ DHT!");
    temperature = 0.0;
    humidity = 0.0;
  }
}

int getGas()
{
  int gasValue = analogRead(GAS_SENSOR_PIN);
  return gasValue;
}

void setLed(bool state)
{
  digitalWrite(LED_PIN, state ? HIGH : LOW);
  Serial.print("Đèn LED đã ");
  Serial.println(state ? "bật" : "tắt");
}

//-------------------- HÀM CẢNH BÁO GAS --------------------//

void alertGas(int gasValue)
{
  static unsigned long previousBuzzerMillis = 0;
  unsigned long currentMillis = millis();

  if (gasValue >= GAS_THRESHOLD)
  {
    if (currentMillis - previousBuzzerMillis >= intervalBuzzer)
    {
      previousBuzzerMillis = currentMillis;
      digitalWrite(BUZZER_PIN, !digitalRead(BUZZER_PIN));
      Serial.println("Cảnh báo: Gas vượt ngưỡng, Buzzer nháy!");
    }
  }
  else
  {
    digitalWrite(BUZZER_PIN, LOW);
  }
}

//-------------------- HÀM MQTT REGISTRATION --------------------//

void sendRegistrationMessage()
{
  if (device_id != "")
  {
    Serial.println("Thiết bị đã được đăng ký, bỏ qua registration");
    return;
  }

  // Lấy thông tin thiết bị từ EEPROM và code, không fix cứng
  // Device Name: configStore.device_name
  // MAC Address: WiFi.macAddress()
  // Device Type: DEVICE_TYPE
  // Active Code: ACTIVE_CODE
  String deviceName = String(configStore.device_name);
  if (deviceName.length() == 0)
  {
    deviceName = "IoT Device " + WiFi.macAddress().substring(12);
  }

  StaticJsonDocument<512> doc;
  doc["mac_address"] = WiFi.macAddress();
  doc["device_name"] = deviceName;
  doc["active_code"] = ACTIVE_CODE;
  doc["device_type"] = DEVICE_TYPE;
  doc["firmware_version"] = FIRMWARE_VERSION;
  doc["sensors"] = "DHT22,GAS_SENSOR";
  doc["capabilities"] = DEVICE_CAPABILITIES;
  doc["wifi_ssid"] = WiFi.SSID();

  String payload;
  serializeJson(doc, payload);

  client.publish("iot/devices/register", payload.c_str());
  Serial.println("Registration message sent:");
  Serial.println(payload);
}

void handleRegistrationResponse(const char *message)
{
  StaticJsonDocument<256> doc;
  DeserializationError error = deserializeJson(doc, message);

  if (!error)
  {
    if (doc.containsKey("device_id"))
    {
      device_id = doc["device_id"].as<String>();
      data_interval = doc["data_interval"] | 30;

      Serial.println("Device registered successfully!");
      Serial.println("Device ID: " + device_id);
      Serial.println("Data interval: " + String(data_interval) + " seconds");

      // Subscribe vào topics riêng của thiết bị
      String configTopic = "iot/devices/" + device_id + "/config";
      String commandsTopic = "iot/devices/" + device_id + "/commands";

      client.subscribe(configTopic.c_str());
      client.subscribe(commandsTopic.c_str());

      Serial.println("Subscribed to: " + configTopic);
      Serial.println("Subscribed to: " + commandsTopic);
    }
  }
  else
  {
    Serial.println("Error parsing registration response");
  }
}

//-------------------- HÀM GỬI DỮ LIỆU MQTT --------------------//

void sendDataToMQTT(float temperature, float humidity, int gasValue, int ledState, int buzzerState)
{
  if (device_id == "" || !device_activated)
  {
    Serial.println("Thiết bị chưa được đăng ký hoặc kích hoạt, không gửi dữ liệu");
    return;
  }

  StaticJsonDocument<512> doc;
  doc["device_id"] = device_id;
  doc["timestamp"] = "2024-01-15T10:30:00Z"; // TODO: Implement real timestamp

  JsonObject sensors = doc.createNestedObject("sensors");
  sensors["temperature"] = temperature;
  sensors["humidity"] = humidity;
  sensors["gas"] = gasValue;

  JsonObject system = doc.createNestedObject("system");
  system["battery_level"] = 85; // Giá trị mặc định
  system["signal_strength"] = WiFi.RSSI();
  system["free_heap"] = ESP.getFreeHeap();
  system["uptime"] = millis() / 1000;
  system["status"] = "ACTIVE";

  String payload;
  serializeJson(doc, payload);

  String dataTopic = "iot/devices/" + device_id + "/data";
  client.publish(dataTopic.c_str(), payload.c_str());
  Serial.println("Data sent to MQTT topic: " + dataTopic);
  Serial.println("Payload: " + payload);
}

//-------------------- MQTT CALLBACK & RECONNECT --------------------//

void callback(char *topic, byte *payload, unsigned int length)
{
  char messageBuff[256];
  if (length >= sizeof(messageBuff))
    length = sizeof(messageBuff) - 1;
  memcpy(messageBuff, payload, length);
  messageBuff[length] = '\0';

  Serial.println("=== MQTT Callback ===");
  Serial.print("Topic: ");
  Serial.println(topic);
  Serial.print("Payload: ");
  Serial.println(messageBuff);

  String topicStr = String(topic);

  // Xử lý registration response
  if (topicStr == "iot/devices/register/response")
  {
    handleRegistrationResponse(messageBuff);
  }

  // Xử lý config updates
  else if (topicStr == "iot/devices/" + device_id + "/config")
  {
    StaticJsonDocument<256> doc;
    DeserializationError error = deserializeJson(doc, messageBuff);
    if (!error)
    {
      if (doc.containsKey("data_interval"))
      {
        data_interval = doc["data_interval"];
        Serial.println("Data interval updated to: " + String(data_interval));
      }
    }
  }

  // Xử lý commands
  else if (topicStr == "iot/devices/" + device_id + "/commands")
  {
    StaticJsonDocument<256> doc;
    DeserializationError error = deserializeJson(doc, messageBuff);
    if (!error)
    {
      String command = doc["command"];

      if (command == "ACTIVATE")
      {
        device_activated = true;
        if (doc.containsKey("config"))
        {
          JsonObject config = doc["config"];
          if (config.containsKey("device_name"))
          {
            device_name = config["device_name"].as<String>();
          }
          if (config.containsKey("data_interval"))
          {
            data_interval = config["data_interval"];
          }
        }
        Serial.println("Device activated!");

        // Gửi confirmation
        String responseTopic = "iot/devices/" + device_id + "/status";
        StaticJsonDocument<128> response;
        response["status"] = "ACTIVATED";
        response["message"] = "Device activated successfully";

        String responsePayload;
        serializeJson(response, responsePayload);
        client.publish(responseTopic.c_str(), responsePayload.c_str());
      }
      else if (command == "RESTART")
      {
        Serial.println("Restart command received");
        ESP.restart();
      }
      else if (command == "LED_CONTROL")
      {
        int ledState = doc["led_state"] | 0;
        setLed(ledState == 1);
      }
    }
  }
}

void reconnectMQTT()
{
  while (!client.connected())
  {
    Serial.print("Kết nối lại MQTT...");
    String clientId = "ESP32Client-" + WiFi.macAddress();

    // Sử dụng cấu hình MQTT từ EEPROM hoặc giá trị mặc định
    const char *mqtt_server = (strlen(configStore.mqtt_server) > 0) ? configStore.mqtt_server : mqtt_server_default;
    const char *mqtt_username = (strlen(configStore.mqtt_username) > 0) ? configStore.mqtt_username : mqtt_username_default;
    const char *mqtt_password = (strlen(configStore.mqtt_password) > 0) ? configStore.mqtt_password : mqtt_password_default;
    int mqtt_port = (strlen(configStore.mqtt_port) > 0) ? atoi(configStore.mqtt_port) : mqtt_port_default;

    Serial.println("MQTT Server: " + String(mqtt_server));
    Serial.println("MQTT Port: " + String(mqtt_port));

    if (client.connect(clientId.c_str(), mqtt_username, mqtt_password))
    {
      Serial.println("Connected.");

      // Subscribe vào registration response topic
      client.subscribe("iot/devices/register/response");

      // Nếu đã có device_id, subscribe vào topics riêng
      if (device_id != "")
      {
        String configTopic = "iot/devices/" + device_id + "/config";
        String commandsTopic = "iot/devices/" + device_id + "/commands";
        client.subscribe(configTopic.c_str());
        client.subscribe(commandsTopic.c_str());
      }
    }
    else
    {
      Serial.print("Failed, rc=");
      Serial.print(client.state());
      Serial.println(" retry sau 5 giây...");
      delay(5000);
    }
  }
}

//-------------------- SETUP & LOOP --------------------//

void setup()
{
  Serial.begin(115200);
  while (!Serial)
    ;

  espConfig.begin();

  Wire.begin();

  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);
  pinMode(BUZZER_PIN, OUTPUT);
  digitalWrite(BUZZER_PIN, LOW);

  dht.begin();

  // Cấu hình MQTT server với port từ EEPROM hoặc mặc định
  int mqtt_port = (strlen(configStore.mqtt_port) > 0) ? atoi(configStore.mqtt_port) : mqtt_port_default;
  const char *mqtt_server = (strlen(configStore.mqtt_server) > 0) ? configStore.mqtt_server : mqtt_server_default;
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);

  Serial.println("IoT Device initialized.");
  Serial.println("Active Code: " + String(ACTIVE_CODE));
  Serial.println("Device Type: " + String(DEVICE_TYPE));
  Serial.println("MAC Address: " + WiFi.macAddress());
}

unsigned long previousMillis = 0;
unsigned long previousDataMillis = 0;

void loop()
{
  espConfig.run();
  app_loop();
  client.loop();
}

void app_loop()
{
  if (espState::is(MODE_RUNNING))
  {
    if (!client.connected())
    {
      reconnectMQTT();
    }

    if (client.connected())
    {
      unsigned long currentMillis = millis();

      // Gửi registration message nếu chưa có device_id
      if (device_id == "" && (currentMillis - last_registration_attempt) > REGISTRATION_RETRY_INTERVAL)
      {
        sendRegistrationMessage();
        last_registration_attempt = currentMillis;
      }

      // Gửi dữ liệu sensor theo interval
      if (device_activated && (currentMillis - previousDataMillis) >= (data_interval * 1000))
      {
        previousDataMillis = currentMillis;

        float temperature, humidity;
        getDHT(temperature, humidity);
        int gasValue = getGas();
        int ledState = digitalRead(LED_PIN);
        int buzzerState = digitalRead(BUZZER_PIN);

        sendDataToMQTT(temperature, humidity, gasValue, ledState, buzzerState);
      }

      // Cảnh báo gas
      int currentGas = getGas();
      alertGas(currentGas);
    }
    else
    {
      Serial.println("MQTT not connected, cannot send data.");
    }
  }
}

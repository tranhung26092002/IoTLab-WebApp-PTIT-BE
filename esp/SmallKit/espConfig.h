extern "C"
{
  void app_loop();
}

#ifdef DEBUG
#define dprint(...) Serial.print(__VA_ARGS__)
#define dprintln(...) Serial.println(__VA_ARGS__)
#else
#define dprint(...)
#define dprintln(...)
#endif

#ifdef ESP32
#include <WiFi.h>
#include <WebServer.h>
WebServer webServer(80);
#else
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <DNSServer.h>
ESP8266WebServer webServer(80);
DNSServer dnsServer;
const byte DNS_PORT = 53;
#endif

#include "configForm.h"
#include <EEPROM.h>
#define EEPROM_CONFIG_START 0 // Starting address to store config
#include <Ticker.h>
Ticker blinker;
#define btSetup 0   // 0
#define ledSignal 2 // 2
volatile bool btSetupPressed = false;
volatile uint32_t btSetupPressTime = -1;
volatile uint32_t blinkTime = millis();
#define btSetupHoldTime 10000
#define WIFI_NET_CONNECT_TIMEOUT 50000
#define WIFI_MAX_RETRIES 500

static int connectNetRetries = WIFI_MAX_RETRIES;

struct ConfigStore
{
  uint8_t flags;
  char ssid_sta[34];
  char pass_sta[64];
  char device_name[64];
  char device_description[128];
  char mqtt_server[64];
  char mqtt_port[8];
  char mqtt_username[32];
  char mqtt_password[32];
} __attribute__((packed));
ConfigStore configStore;
const ConfigStore configDefault = {
    0x00,
    "",
    ""};

template <typename T, int size>
void copyString(const String &s, T (&arr)[size])
{
  s.toCharArray(arr, size);
}

void restartMCU()
{
  ESP.restart();
  delay(10000);
  while (1)
  {
  };
}

enum State
{
  MODE_WAIT_CONFIG,
  MODE_CONFIGURING,
  MODE_CONNECTING_NET,
  MODE_RUNNING,
  MODE_SWITCH_TO_STA,
  MODE_RESET_CONFIG,
  MODE_ERROR,

  MODE_MAX_VALUE
};
const char *StateStr[MODE_MAX_VALUE + 1] = {
    "WAIT_CONFIG",
    "CONFIGURING",
    "CONNECTING_NET",
    "RUNNING",
    "SWITCH_TO_STA",
    "RESET_CONFIG",
    "ERROR",

    "INIT"};
namespace espState
{
  volatile State state = MODE_MAX_VALUE;

  State get()
  {
    return state;
  }
  bool is(State m)
  {
    return (state == m);
  }
  void set(State m);
};
inline void espState::set(State m)
{
  if (state != m && m < MODE_MAX_VALUE)
  {
    dprintln(String(StateStr[state]) + " => " + StateStr[m]);
    state = m;
  }
}

bool configSave()
{
  EEPROM.put(EEPROM_CONFIG_START, configStore);
  EEPROM.commit();
  dprintln("Configuration stored to flash");
  return true;
}

void configLoad()
{
  dprintln("Load Configuration stored");
  memset(&configStore, 0, sizeof(configStore));
  EEPROM.get(EEPROM_CONFIG_START, configStore);
  dprintln("Flags: " + String(configStore.flags));
  dprintln("WiFi SSID: " + String(configStore.ssid_sta));
  dprintln("Password: " + String(configStore.pass_sta));
  dprintln("Device Name: " + String(configStore.device_name));
  dprintln("Device Description: " + String(configStore.device_description));
  dprintln("MQTT Server: " + String(configStore.mqtt_server));
  dprintln("MQTT Port: " + String(configStore.mqtt_port));
  dprintln("MQTT Username: " + String(configStore.mqtt_username));
}

bool configInit()
{
  EEPROM.begin(sizeof(ConfigStore) + EEPROM_CONFIG_START);
  dprintln("EEPROM config size: " + String(sizeof(ConfigStore)));
  configLoad();
  return true;
}

void blinkLed(uint32_t t)
{
  if (millis() - blinkTime > t)
  {
    digitalWrite(ledSignal, !digitalRead(ledSignal));
    blinkTime = millis();
  }
}

void ledSignalControl()
{
  State currState = espState::get();
  if (btSetupPressed && (millis() - btSetupPressTime) > btSetupHoldTime)
  {
    digitalWrite(ledSignal, !digitalRead(ledSignal));
  }
  else if (btSetupPressed)
  {
    blinkLed(1000);
  }
  else if (currState == MODE_WAIT_CONFIG)
  {
    blinkLed(200);
  }
  else if (currState == MODE_CONNECTING_NET)
  {
    blinkLed(500);
  }
  else if (currState == MODE_RUNNING)
  {
    blinkLed(5000);
  }
}

void enterResetConfig()
{
  dprintln("ESP is reset to default!");
  configStore = configDefault;
  configSave();
  espState::set(MODE_WAIT_CONFIG);
}

ICACHE_RAM_ATTR void btSetupChange()
{
  bool btState = !digitalRead(btSetup);
  if (btState && !btSetupPressed)
  {
    btSetupPressTime = millis();
    btSetupPressed = true;
    dprintln("Hold the button for 10 seconds to reset default...");
    digitalWrite(ledSignal, HIGH);
  }
  else if (!btState && btSetupPressed)
  {
    digitalWrite(ledSignal, LOW);
    btSetupPressed = false;
    uint32_t btHoldTime = millis() - btSetupPressTime;
    if (btHoldTime >= btSetupHoldTime)
    {
      espState::set(MODE_RESET_CONFIG);
    }
    btSetupPressTime = -1;
  }
}

void enterConfigMode()
{
  WiFi.mode(WIFI_OFF);
  delay(100);
  WiFi.mode(WIFI_AP);

  // Đặt tên Wi-Fi mặc định là Node_01
  String ssid_ap = "Gateway_01";
  WiFi.softAP(ssid_ap.c_str(), "12345678"); // Mật khẩu cho AP là "12345678"
  delay(500);                               // Chờ để AP hoạt động

  // In thông tin SSID và IP của AP
  dprintln("AP SSID: " + ssid_ap);
  dprintln("AP IP: " + WiFi.softAPIP().toString());

#ifdef ESP8266
  dnsServer.start(DNS_PORT, "*", WiFi.softAPIP());
#endif

  webServer.on("/", []()
               { webServer.send(200, "text/html", configForm); });
  webServer.on("/wifiscan.json", []()
               {
    dprintln("Scanning networks...");
    int wifi_nets = WiFi.scanNetworks(true, true);
    const uint32_t t = millis();
    while (wifi_nets < 0 && millis() - t < 20000) {
      delay(20);
      wifi_nets = WiFi.scanComplete();
    }
    dprintln(String("Found networks: ") + wifi_nets);
    if (wifi_nets > 0) {
      String ssidList = "[\"";
      for (int i = 0; i < wifi_nets; ++i) {
        ssidList += WiFi.SSID(i) + "\"";
        if (i < (wifi_nets - 1)) {
          ssidList += ",\"";
        }
      }
      ssidList += "]";
      webServer.send(200, "application/json", ssidList);
    } else {
      webServer.send(200, "application/json", "[]");
    } });
  webServer.on("/save-config", []()
               {
      dprintln("Applying IoT device configuration...");
      String ssid = webServer.arg("ssid");
      String pass = webServer.arg("password");
      String device_name = webServer.arg("device_name");
      String device_description = webServer.arg("device_description");
      String mqtt_server = webServer.arg("mqtt_server");
      String mqtt_port = webServer.arg("mqtt_port");
      String mqtt_username = webServer.arg("mqtt_username");
      String mqtt_password = webServer.arg("mqtt_password");
      String content;
      if (ssid.length() > 0 && mqtt_server.length() > 0) {
        configStore.flags = 0x01;
        copyString(ssid, configStore.ssid_sta);
        copyString(pass, configStore.pass_sta);
        copyString(device_name, configStore.device_name);
        copyString(device_description, configStore.device_description);
        copyString(mqtt_server, configStore.mqtt_server);
        copyString(mqtt_port, configStore.mqtt_port);
        copyString(mqtt_username, configStore.mqtt_username);
        copyString(mqtt_password, configStore.mqtt_password);
        configSave();
        content = "Configuration saved successfully";
        dprintln("Device name: " + device_name);
        dprintln("Device description: " + device_description);
        dprintln("MQTT Server: " + mqtt_server);
        dprintln("MQTT Port: " + mqtt_port);
      } else {
        dprintln("Configuration invalid");
        content = "Configuration invalid";
      }
      webServer.send(200, "application/json", content);
      connectNetRetries = 1;
      espState::set(MODE_SWITCH_TO_STA); });
  webServer.on("/reboot", []()
               { restartMCU(); });
  webServer.onNotFound([]()
                       { webServer.send(200, "text/html", configForm); });
  webServer.begin();

  while (espState::is(MODE_WAIT_CONFIG) || espState::is(MODE_CONFIGURING))
  {
    app_loop();
    delay(10);
    webServer.handleClient();
#ifdef ESP8266
    dnsServer.processNextRequest();
#endif
    if (espState::is(MODE_CONFIGURING) && WiFi.softAPgetStationNum() == 0)
    {
      espState::set(MODE_WAIT_CONFIG);
    }
  }
  webServer.stop();
}

void enterSwitchToSTA()
{
  espState::set(MODE_SWITCH_TO_STA);
  dprintln("Switching to STA...");
  delay(1000);
  WiFi.mode(WIFI_OFF);
  delay(100);
  WiFi.mode(WIFI_STA);
  espState::set(MODE_CONNECTING_NET);
}

void enterConnectNet()
{
  espState::set(MODE_CONNECTING_NET);
  dprintln(String("Connecting to WiFi: ") + configStore.ssid_sta);
  WiFi.mode(WIFI_STA);

  if (!WiFi.begin(configStore.ssid_sta, configStore.pass_sta))
  {
    espState::set(MODE_ERROR);
    return;
  }
  int n = 0;
  unsigned long timeoutMs = millis() + WIFI_NET_CONNECT_TIMEOUT;
  while ((timeoutMs > millis()) && (WiFi.status() != WL_CONNECTED))
  {
    app_loop();
    delay(10);
    dprint(".");
    n++;
    if (n == 40)
    {
      dprintln();
      n = 0;
    }
    if (!espState::is(MODE_CONNECTING_NET))
    {
      WiFi.disconnect();
      return;
    }
  }

  if (WiFi.status() == WL_CONNECTED)
  {
    dprintln("\nConnected to WiFi, IP: " + WiFi.localIP().toString());
    espState::set(MODE_RUNNING);
    connectNetRetries = WIFI_MAX_RETRIES;
  }
  else if (--connectNetRetries <= 0)
  {
    dprintln();
    espState::set(MODE_ERROR);
  }
}

void enterError()
{
  espState::set(MODE_ERROR);

  unsigned long timeoutMs = millis() + 10000;
  while (timeoutMs > millis() || btSetupPressed)
  {
    delay(10);
    if (!espState::is(MODE_ERROR))
    {
      return;
    }
  }
  dprintln("Restarting after error.");
  delay(10);
  restartMCU();
}

class Config
{
public:
  void begin()
  {
    dprintln("\n----------------ESP Config--------------");

    pinMode(btSetup, INPUT_PULLUP);
    pinMode(ledSignal, OUTPUT);
    digitalWrite(ledSignal, LOW);

    blinker.attach_ms(100, ledSignalControl);
    attachInterrupt(btSetup, btSetupChange, CHANGE);

    configInit();

    if (configStore.flags == 0x01)
    {
      espState::set(MODE_CONNECTING_NET);
    }
    else
    {
      espState::set(MODE_WAIT_CONFIG);
    }
  }
  void run()
  {
    switch (espState::get())
    {
    case MODE_WAIT_CONFIG:
    case MODE_CONFIGURING:
      enterConfigMode();
      break;
    case MODE_CONNECTING_NET:
      enterConnectNet();
      break;
    case MODE_RUNNING:
      delay(100);
      break;
    case MODE_SWITCH_TO_STA:
      enterSwitchToSTA();
      break;
    case MODE_RESET_CONFIG:
      enterResetConfig();
      break;
    default:
      enterError();
      break;
    }
  }
  void restartMCU()
  {
    ESP.restart();
    delay(10000);
    while (1)
    {
    };
  }
} espConfig;

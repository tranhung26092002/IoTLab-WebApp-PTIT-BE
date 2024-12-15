package com.ptit.service.domain.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.entities.SensorData;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MqttService extends BaseService{

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SensorDataService sensorDataService;
    @Autowired
    private NotificationService notificationService;

    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing MQTT Service...");
            mqttClient.subscribe("iot/data", this::handleDeviceDataMessage);
            logger.info("Subscribed to topic: iot/data");
            // Subscribe nhận phản hồi lệnh
            mqttClient.subscribe("iot/command-response/#", this::handleCommandResponse);
        } catch (MqttException e) {
            logger.error("Error subscribing to MQTT topic", e);
        }
    }

    private void handleDeviceDataMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            logger.debug("Payload: {}", payload);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(payload);

            String deviceId = jsonNode.path("device_id").asText("N/A");

            Device device = deviceService.findByDeviceId(deviceId);

            if (device == null) {
                device = new Device();
                device.setDeviceId(deviceId);
                device.setName(jsonNode.path("device_name").asText("N/A"));
                device.setType("device");
                device.setLocation("Ha Noi");
                device = deviceService.save(device);
            }

            JsonNode sensorsNode = jsonNode.path("sensors");
            float temperature = sensorsNode.path("temperature").floatValue();
            float humidity = sensorsNode.path("humidity").floatValue();
            float light = sensorsNode.path("light").floatValue();
            float gas = sensorsNode.path("gas").floatValue();

            SensorData sensorData = new SensorData();
            sensorData.setDevice(device);
            sensorData.setTemperature(temperature);
            sensorData.setHumidity(humidity);
            sensorData.setLight(light);
            sensorData.setGas(gas);

            JsonNode statusNode = jsonNode.path("status");
            int led = statusNode.path("led").intValue();
            int fan = statusNode.path("fan").intValue();
            int alertLed = statusNode.path("alert_led").intValue();
            int buzzer = statusNode.path("buzzer").intValue();
            int servo = statusNode.path("servo").intValue();

            sensorData.setLed(led);
            sensorData.setFan(fan);
            sensorData.setAlertLed(alertLed);
            sensorData.setBuzzer(buzzer);
            sensorData.setServo(servo);

            sensorDataService.save(sensorData);

            notificationService.sendRealtimeUpdate("/topic/sensorData/"+ device.getId() , sensorData);
            logger.debug("Sending data to /topic/sensorData/{}: {}", device.getId(), sensorData);
            System.out.printf("Published : %s\n", sensorData);
        } catch (Exception e) {
            logger.error("Error while processing message", e);
        }
    }

    // Xử lý phản hồi từ thiết bị
    private void handleCommandResponse(String topic, MqttMessage message) {
        try {
            String responsePayload = new String(message.getPayload());
            logger.info("Received command response: {}", responsePayload);

            // Lấy deviceId từ topic (iot/command-response/{deviceId})
            String[] topicParts = topic.split("/");
            if (topicParts.length >= 3) {
                String deviceId = topicParts[2];

                // Gửi dữ liệu phản hồi qua WebSocket
                notificationService.sendRealtimeUpdate("/topic/command-response/" + deviceId, responsePayload);
                logger.info("Sent command response to WebSocket for device: {}", deviceId);
            }
        } catch (Exception e) {
            logger.error("Error processing command response message", e);
        }
    }
}

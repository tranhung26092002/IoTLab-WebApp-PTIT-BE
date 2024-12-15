package com.ptit.service.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.entities.SensorData;
import com.ptit.service.domain.services.DeviceService;
import com.ptit.service.domain.services.SensorDataService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {

    @Autowired
    private SensorDataService sensorDataService;
    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/device/{deviceId}")
    @SendTo("/topic/sensorData/{deviceId}")
    public SensorData getDeviceData(@DestinationVariable Long deviceId) {
        return sensorDataService.getLatestData(deviceId);
    }

    // Subscribe to real-time updates for a specific deivce
    @MessageMapping("/subscribe/device/{deviceId}")
    public void subscribeToDevice(@DestinationVariable Long deviceId) {
        Device device = deviceService.findById(deviceId);
        if (device != null) {
            messagingTemplate.convertAndSend("/topic/sensorData/" + deviceId, sensorDataService.getLatestData(deviceId));
        }
    }

    // Get historical data for a Device
    @MessageMapping("/device/history/{deviceId}")
    @SendTo("/topic/history/{deviceId}")
    public Page<SensorData> getDeviceHistory(@DestinationVariable Long deviceId, Pageable pageable) {
        return sensorDataService.getHistory(deviceId, pageable);
    }

    @MessageMapping("/publish/command/{deviceId}")
    public void publishCommand(@DestinationVariable Long deviceId, @Payload String commandJson) {
        // Nhận lệnh từ client và gửi lệnh đến thiết bị
        log.info("Received command: {}", commandJson);

        // Giả sử commandJson có cấu trúc như { "deviceName": "device_1", "led": 1 }
        try {
            // Parse JSON command để lấy trạng thái của các thiết bị
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode commandNode = objectMapper.readTree(commandJson);
            String deviceName = commandNode.path("deviceName").asText();

            // Xử lý các thiết bị trong lệnh
            Device device = deviceService.findById(deviceId);
            if (device != null) {
                // Tạo thông điệp MQTT chứa các trạng thái thiết bị
                ObjectNode mqttMessage = objectMapper.createObjectNode();

                // Lặp qua tất cả các trường trong lệnh (ngoại trừ "deviceName")
                commandNode.fieldNames().forEachRemaining(field -> {
                    if (!field.equals("deviceName")) {
                        mqttMessage.put(field, commandNode.path(field).asInt()); // Thêm các thiết bị (led, buzzer, etc.)
                    }
                });

                // Gửi thông điệp MQTT đến thiết bị
                String mqttCommand = mqttMessage.toString();
                try {
                    mqttClient.publish("iot/command/" + device.getDeviceId(), mqttCommand.getBytes(), 2, false);
                    log.info("Command published to MQTT: {}", mqttCommand);
                } catch (Exception e) {
                    log.error("Error publishing command", e);
                }
            }
        } catch (Exception e) {
            log.error("Error processing command JSON", e);
        }
    }

    @MessageMapping("/subscribe/command-response/{deviceId}")
    public void subscribeToCommandResponse(@DestinationVariable Long deviceId) {
        log.info("Subscribed to command response for device ID: {}", deviceId);
        try {
            mqttClient.subscribe("iot/command-response/" + deviceId, (topic, message) -> {
                String responsePayload = new String(message.getPayload());
                log.info("Received command response for device {}: {}", deviceId, responsePayload);
                // Gửi dữ liệu phản hồi tới frontend qua WebSocket
                messagingTemplate.convertAndSend("/topic/command-response/" + deviceId, responsePayload);
            });
        } catch (Exception e) {
            log.error("Error subscribing to command-response topic for device {}", deviceId, e);
        }
    }
}

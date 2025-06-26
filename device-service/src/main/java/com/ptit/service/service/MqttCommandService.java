package com.ptit.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.IotDeviceCommand;
import com.ptit.service.entity.IotDeviceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MqttCommandService {

    @Autowired
    private MqttPahoMessageHandler mqttOutboundHandler;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Gửi lệnh kích hoạt thiết bị
     */
    public void sendActivationCommand(Device device, IotDeviceConfig config) {
        try {
            Map<String, Object> commandData = new HashMap<>();
            commandData.put("command", "ACTIVATE");
            commandData.put("device_id", device.getCode());
            commandData.put("config", Map.of(
                    "device_name", device.getName(),
                    "data_interval", config.getDataInterval(),
                    "alert_thresholds", config.getAlertThresholds(),
                    "display_config", config.getDisplayConfig()
            ));

            String commandJson = objectMapper.writeValueAsString(commandData);
            String topic = "iot/devices/" + device.getCode() + "/commands";

            Message<String> message = MessageBuilder
                    .withPayload(commandJson)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .build();

            mqttOutboundHandler.handleMessage(message);
            log.info("Activation command sent to device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending activation command: {}", e.getMessage(), e);
        }
    }

    /**
     * Gửi lệnh deactivate thiết bị
     */
    public void sendDeactivationCommand(Device device) {
        try {
            Map<String, Object> commandData = new HashMap<>();
            commandData.put("command", "DEACTIVATE");
            commandData.put("device_id", device.getCode());

            String commandJson = objectMapper.writeValueAsString(commandData);
            String topic = "iot/devices/" + device.getCode() + "/commands";

            Message<String> message = MessageBuilder
                    .withPayload(commandJson)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .build();

            mqttOutboundHandler.handleMessage(message);
            log.info("Deactivation command sent to device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending deactivation command: {}", e.getMessage(), e);
        }
    }

    /**
     * Gửi lệnh restart thiết bị
     */
    public void sendRestartCommand(Device device) {
        try {
            Map<String, Object> commandData = new HashMap<>();
            commandData.put("command", "RESTART");
            commandData.put("device_id", device.getCode());

            String commandJson = objectMapper.writeValueAsString(commandData);
            String topic = "iot/devices/" + device.getCode() + "/commands";

            Message<String> message = MessageBuilder
                    .withPayload(commandJson)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .build();

            mqttOutboundHandler.handleMessage(message);
            log.info("Restart command sent to device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending restart command: {}", e.getMessage(), e);
        }
    }

    /**
     * Gửi lệnh cập nhật cấu hình
     */
    public void sendConfigUpdateCommand(Device device, IotDeviceConfig config) {
        try {
            Map<String, Object> commandData = new HashMap<>();
            commandData.put("command", "UPDATE_CONFIG");
            commandData.put("device_id", device.getCode());
            commandData.put("config", Map.of(
                    "data_interval", config.getDataInterval(),
                    "alert_thresholds", config.getAlertThresholds(),
                    "display_config", config.getDisplayConfig()
            ));

            String commandJson = objectMapper.writeValueAsString(commandData);
            String topic = "iot/devices/" + device.getCode() + "/commands";

            Message<String> message = MessageBuilder
                    .withPayload(commandJson)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .build();

            mqttOutboundHandler.handleMessage(message);
            log.info("Config update command sent to device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending config update command: {}", e.getMessage(), e);
        }
    }
} 
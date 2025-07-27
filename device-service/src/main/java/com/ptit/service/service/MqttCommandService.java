package com.ptit.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.IotDeviceCommand;
import com.ptit.service.entity.IotDeviceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MqttCommandService {

    @Autowired
    private MqttClient mqttClient;

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
            Map<String, Object> configMap = new HashMap<>();
            configMap.put("device_name", device.getName());
            configMap.put("data_interval", config.getDataInterval());
            configMap.put("alert_thresholds", config.getAlertThresholds());
            configMap.put("display_config", config.getDisplayConfig());
            commandData.put("config", configMap);

            String commandJson = objectMapper.writeValueAsString(commandData);
            String topic = "iot/devices/" + device.getCode() + "/commands";

            mqttClient.publish(topic, new MqttMessage(commandJson.getBytes(StandardCharsets.UTF_8)));
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

            mqttClient.publish(topic, new MqttMessage(commandJson.getBytes(StandardCharsets.UTF_8)));
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

            mqttClient.publish(topic, new MqttMessage(commandJson.getBytes(StandardCharsets.UTF_8)));
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
            Map<String, Object> configMap = new HashMap<>();
            configMap.put("data_interval", config.getDataInterval());
            configMap.put("alert_thresholds", config.getAlertThresholds());
            configMap.put("display_config", config.getDisplayConfig());
            commandData.put("config", configMap);

            String commandJson = objectMapper.writeValueAsString(commandData);
            String topic = "iot/devices/" + device.getCode() + "/commands";

            mqttClient.publish(topic, new MqttMessage(commandJson.getBytes(StandardCharsets.UTF_8)));
            log.info("Config update command sent to device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending config update command: {}", e.getMessage(), e);
        }
    }

    /**
     * Gửi lệnh tùy chỉnh
     */
    public void sendCustomCommand(Device device, String command) {
        try {
            Map<String, Object> commandData = new HashMap<>();
            commandData.put("command", "CUSTOM");
            commandData.put("device_id", device.getCode());
            commandData.put("data", command);

            String commandJson = objectMapper.writeValueAsString(commandData);
            String topic = "iot/devices/" + device.getCode() + "/commands";

            mqttClient.publish(topic, new MqttMessage(commandJson.getBytes(StandardCharsets.UTF_8)));
            log.info("Custom command sent to device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending custom command: {}", e.getMessage(), e);
        }
    }
}
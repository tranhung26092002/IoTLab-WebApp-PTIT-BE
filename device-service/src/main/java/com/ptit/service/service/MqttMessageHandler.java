package com.ptit.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.dto.IotDeviceRegistrationDTO;
import com.ptit.service.dto.IotSensorDataDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.IotSensorData;
import com.ptit.service.entity.enums.DeviceStatus;
import com.ptit.service.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MqttMessageHandler {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private IotDeviceService iotDeviceService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private ObjectMapper objectMapper;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            String payload = message.getPayload().toString();
            
            log.info("Received MQTT message on topic: {} with payload: {}", topic, payload);
            
            if (topic.equals("iot/devices/register")) {
                handleDeviceRegistration(payload);
            } else if (topic.matches("iot/devices/\\w+/data")) {
                handleSensorData(topic, payload);
            } else if (topic.matches("iot/devices/\\w+/status")) {
                handleDeviceStatus(topic, payload);
            }
        } catch (Exception e) {
            log.error("Error processing MQTT message: {}", e.getMessage(), e);
        }
    }

    private void handleDeviceRegistration(String payload) {
        try {
            IotDeviceRegistrationDTO registrationDTO = objectMapper.readValue(payload, IotDeviceRegistrationDTO.class);
            
            // Check if device already exists
            Device existingDevice = deviceRepository.findByMacAddress(registrationDTO.getMacAddress()).orElse(null);
            
            if (existingDevice != null) {
                log.info("Device with MAC {} already exists, updating registration", registrationDTO.getMacAddress());
                // Update existing device
                existingDevice.setActiveCode(registrationDTO.getActiveCode());
                existingDevice.setFirmwareVersion(registrationDTO.getFirmwareVersion());
                existingDevice.setWifiSsid(registrationDTO.getWifiSsid());
                existingDevice.setLastSeen(LocalDateTime.now());
                deviceRepository.save(existingDevice);
            } else {
                // Create new IoT device
                Device newDevice = new Device();
                newDevice.setName(registrationDTO.getDeviceName());
                newDevice.setType(registrationDTO.getDeviceType());
                newDevice.setDescription("Auto-registered IoT device");
                newDevice.setActiveCode(registrationDTO.getActiveCode());
                newDevice.setMacAddress(registrationDTO.getMacAddress());
                newDevice.setFirmwareVersion(registrationDTO.getFirmwareVersion());
                newDevice.setWifiSsid(registrationDTO.getWifiSsid());
                newDevice.setIotDevice(true);
                newDevice.setStatus(DeviceStatus.REGISTERED);
                newDevice.setLastSeen(LocalDateTime.now());
                
                deviceRepository.save(newDevice);
                log.info("New IoT device registered: {}", newDevice.getCode());
                
                // Gửi thông báo thiết bị mới được phát hiện qua WebSocket
                webSocketService.sendDeviceDiscovered(newDevice.getCode(), newDevice.getName());
            }
            
            // Send registration response
            sendRegistrationResponse(registrationDTO.getMacAddress());
            
        } catch (Exception e) {
            log.error("Error handling device registration: {}", e.getMessage(), e);
        }
    }

    private void handleSensorData(String topic, String payload) {
        try {
            IotSensorDataDTO sensorDataDTO = objectMapper.readValue(payload, IotSensorDataDTO.class);
            
            // Extract device ID from topic
            String deviceId = topic.split("/")[2];
            
            // Find device by code
            Device device = deviceRepository.findByCode(deviceId).orElse(null);
            if (device != null && device.isIotDevice()) {
                IotSensorData savedData = iotDeviceService.saveSensorData(device, sensorDataDTO);
                log.info("Sensor data saved for device: {}", deviceId);
                
                // Gửi dữ liệu sensor realtime qua WebSocket
                webSocketService.sendSensorData(savedData);
            }
            
        } catch (Exception e) {
            log.error("Error handling sensor data: {}", e.getMessage(), e);
        }
    }

    private void handleDeviceStatus(String topic, String payload) {
        try {
            // Extract device ID from topic
            String deviceId = topic.split("/")[2];
            
            Device device = deviceRepository.findByCode(deviceId).orElse(null);
            if (device != null && device.isIotDevice()) {
                device.setLastSeen(LocalDateTime.now());
                deviceRepository.save(device);
                log.info("Device status updated for: {}", deviceId);
                
                // Gửi thông báo trạng thái thiết bị qua WebSocket
                webSocketService.sendDeviceStatus(deviceId, device.getStatus().toString(), "Device status updated");
            }
            
        } catch (Exception e) {
            log.error("Error handling device status: {}", e.getMessage(), e);
        }
    }

    private void sendRegistrationResponse(String macAddress) {
        try {
            Device device = deviceRepository.findByMacAddress(macAddress).orElse(null);
            if (device != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("device_id", device.getCode());
                response.put("status", "registered");
                response.put("data_interval", 30);
                response.put("server_time", System.currentTimeMillis());
                
                String responseJson = objectMapper.writeValueAsString(response);
                // This would be sent via MQTT outbound channel
                log.info("Registration response sent: {}", responseJson);
            }
        } catch (Exception e) {
            log.error("Error sending registration response: {}", e.getMessage(), e);
        }
    }
} 
package com.ptit.service.service;

import com.ptit.service.dto.IotSensorDataDTO;
import com.ptit.service.entity.IotSensorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi dữ liệu sensor realtime đến frontend
     */
    public void sendSensorData(IotSensorData sensorData) {
        IotSensorDataDTO dto = convertToDTO(sensorData);
        
        // Gửi đến topic chung cho tất cả thiết bị
        messagingTemplate.convertAndSend("/iot/sensor-data", dto);
        
        // Gửi đến topic riêng cho từng thiết bị
        messagingTemplate.convertAndSend("/iot/device/" + sensorData.getDeviceId() + "/sensor-data", dto);
    }

    /**
     * Gửi thông báo trạng thái thiết bị
     */
    public void sendDeviceStatus(String deviceId, String status, String message) {
        DeviceStatusUpdate statusUpdate = new DeviceStatusUpdate(deviceId, status, message);
        messagingTemplate.convertAndSend("/iot/device-status", statusUpdate);
        messagingTemplate.convertAndSend("/iot/device/" + deviceId + "/status", statusUpdate);
    }

    /**
     * Gửi thông báo thiết bị mới được phát hiện
     */
    public void sendDeviceDiscovered(String deviceId, String deviceName) {
        DeviceDiscovery discovery = new DeviceDiscovery(deviceId, deviceName);
        messagingTemplate.convertAndSend("/iot/device-discovered", discovery);
    }

    /**
     * Gửi thông báo thiết bị được kích hoạt
     */
    public void sendDeviceActivated(String deviceId, String deviceName) {
        DeviceActivation activation = new DeviceActivation(deviceId, deviceName);
        messagingTemplate.convertAndSend("/iot/device-activated", activation);
    }

    private IotSensorDataDTO convertToDTO(IotSensorData sensorData) {
        // Create sensors map
        Map<String, Object> sensors = new HashMap<>();
        sensors.put("temperature", sensorData.getTemperature());
        sensors.put("humidity", sensorData.getHumidity());
        sensors.put("light", sensorData.getLight());
        
        // Create system map
        Map<String, Object> system = new HashMap<>();
        system.put("battery_level", sensorData.getBatteryLevel());
        system.put("signal_strength", sensorData.getSignalStrength());
        
        return IotSensorDataDTO.builder()
                .deviceId(sensorData.getDeviceId())
                .timestamp(sensorData.getTimestamp())
                .sensors(sensors)
                .system(system)
                .build();
    }

    // Inner classes for WebSocket messages
    public static class DeviceStatusUpdate {
        private String deviceId;
        private String status;
        private String message;
        private long timestamp;

        public DeviceStatusUpdate(String deviceId, String status, String message) {
            this.deviceId = deviceId;
            this.status = status;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getDeviceId() { return deviceId; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    public static class DeviceDiscovery {
        private String deviceId;
        private String deviceName;
        private long timestamp;

        public DeviceDiscovery(String deviceId, String deviceName) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getDeviceId() { return deviceId; }
        public String getDeviceName() { return deviceName; }
        public long getTimestamp() { return timestamp; }
    }

    public static class DeviceActivation {
        private String deviceId;
        private String deviceName;
        private long timestamp;

        public DeviceActivation(String deviceId, String deviceName) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getDeviceId() { return deviceId; }
        public String getDeviceName() { return deviceName; }
        public long getTimestamp() { return timestamp; }
    }
}
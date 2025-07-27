package com.ptit.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.dto.IotDeviceActivationDTO;
import com.ptit.service.dto.IotSensorDataDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.IotDeviceConfig;
import com.ptit.service.entity.IotDeviceCommand;
import com.ptit.service.entity.IotSensorData;
import com.ptit.service.entity.enums.DeviceStatus;
import com.ptit.service.repository.DeviceRepository;
import com.ptit.service.repository.IotDeviceCommandRepository;
import com.ptit.service.repository.IotDeviceConfigRepository;
import com.ptit.service.repository.IotSensorDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IotDeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private IotSensorDataRepository sensorDataRepository;

    @Autowired
    private IotDeviceConfigRepository configRepository;

    @Autowired
    private IotDeviceCommandRepository commandRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private MqttCommandService mqttCommandService;

    @Autowired
    private ObjectMapper objectMapper;

    public IotSensorData saveSensorData(Device device, IotSensorDataDTO sensorDataDTO) {
        try {
            IotSensorData sensorData = new IotSensorData();
            sensorData.setDevice(device);
            sensorData.setDeviceId(device.getCode());
            sensorData.setTimestamp(sensorDataDTO.getTimestamp());

            // Extract sensor values
            if (sensorDataDTO.getSensors() != null) {
                sensorData.setTemperature((Double) sensorDataDTO.getSensors().get("temperature"));
                sensorData.setHumidity((Double) sensorDataDTO.getSensors().get("humidity"));
                sensorData.setPressure((Double) sensorDataDTO.getSensors().get("pressure"));
                sensorData.setLight((Double) sensorDataDTO.getSensors().get("light"));
            }

            // Extract system values
            if (sensorDataDTO.getSystem() != null) {
                sensorData.setBatteryLevel((Integer) sensorDataDTO.getSystem().get("battery_level"));
                sensorData.setSignalStrength((Integer) sensorDataDTO.getSystem().get("signal_strength"));
            }

            // Store raw data as JSON
            sensorData.setRawData(objectMapper.writeValueAsString(sensorDataDTO));

            IotSensorData savedData = sensorDataRepository.save(sensorData);

            // Update device last seen
            device.setLastSeen(LocalDateTime.now());
            deviceRepository.save(device);

            return savedData;

        } catch (Exception e) {
            log.error("Error saving sensor data: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean activateDevice(IotDeviceActivationDTO activationDTO) {
        try {
            Device device = deviceRepository.findByActiveCode(activationDTO.getActiveCode()).orElse(null);
            if (device == null || !device.isIotDevice()) {
                log.error("Device not found or not an IoT device for active code: {}", activationDTO.getActiveCode());
                return false;
            }

            // Update device information
            device.setName(activationDTO.getDeviceName());
            device.setDescription(activationDTO.getDescription());
            device.setStatus(DeviceStatus.ACTIVE);
            device.setActivatedAt(LocalDateTime.now());
            // device.setActivatedBy(getCurrentUserId()); // TODO: Implement user
            // authentication

            deviceRepository.save(device);

            // Create or update device configuration
            IotDeviceConfig config = configRepository.findByDeviceId(device.getId()).orElse(new IotDeviceConfig());
            config.setDevice(device);
            config.setDataInterval(activationDTO.getDataInterval());
            config.setAlertThresholds(activationDTO.getAlertThresholds());
            config.setDisplayConfig(activationDTO.getDisplayConfig());

            configRepository.save(config);

            // Send activation command to device
            sendActivationCommand(device);

            // Gửi thông báo kích hoạt thiết bị qua WebSocket
            webSocketService.sendDeviceActivated(device.getCode(), device.getName());

            log.info("Device activated successfully: {}", device.getCode());
            return true;

        } catch (Exception e) {
            log.error("Error activating device: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<Device> getAllIotDevices() {
        return deviceRepository.findAllIotDevices();
    }

    public List<Device> getActiveIotDevices() {
        return deviceRepository.findActiveIotDevices();
    }

    public List<Device> getRegisteredIotDevices() {
        return deviceRepository.findRegisteredIotDevices();
    }

    public Optional<IotSensorData> getLatestSensorData(Long deviceId) {
        return sensorDataRepository.findFirstByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public List<IotSensorData> getSensorDataHistory(Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        return sensorDataRepository.findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(deviceId, startTime, endTime);
    }

    public boolean deactivateDevice(Long deviceId) {
        try {
            Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null || !device.isIotDevice()) {
                return false;
            }

            device.setStatus(DeviceStatus.DEACTIVATED);
            deviceRepository.save(device);

            // Send deactivation command
            sendDeactivationCommand(device);

            return true;
        } catch (Exception e) {
            log.error("Error deactivating device: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean restartDevice(Long deviceId) {
        try {
            Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null || !device.isIotDevice()) {
                return false;
            }

            // Send restart command
            sendRestartCommand(device);

            return true;
        } catch (Exception e) {
            log.error("Error restarting device: {}", e.getMessage(), e);
            return false;
        }
    }

    private void sendActivationCommand(Device device) {
        try {
            IotDeviceCommand command = new IotDeviceCommand();
            command.setDevice(device);
            command.setCommand("ACTIVATE");

            // Create command data
            IotDeviceConfig config = configRepository.findByDeviceId(device.getId()).orElse(null);
            if (config != null) {
                command.setCommandData(objectMapper.writeValueAsString(config));
                // Send command via MQTT
                mqttCommandService.sendActivationCommand(device, config);
            }

            command.setStatus("SENT");
            commandRepository.save(command);

            log.info("Activation command sent for device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending activation command: {}", e.getMessage(), e);
        }
    }

    private void sendDeactivationCommand(Device device) {
        try {
            IotDeviceCommand command = new IotDeviceCommand();
            command.setDevice(device);
            command.setCommand("DEACTIVATE");
            command.setStatus("SENT");
            commandRepository.save(command);

            // Send command via MQTT
            mqttCommandService.sendDeactivationCommand(device);

            log.info("Deactivation command sent for device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending deactivation command: {}", e.getMessage(), e);
        }
    }

    private void sendRestartCommand(Device device) {
        try {
            IotDeviceCommand command = new IotDeviceCommand();
            command.setDevice(device);
            command.setCommand("RESTART");
            command.setStatus("SENT");
            commandRepository.save(command);

            // Send command via MQTT
            mqttCommandService.sendRestartCommand(device);

            log.info("Restart command sent for device: {}", device.getCode());

        } catch (Exception e) {
            log.error("Error sending restart command: {}", e.getMessage(), e);
        }
    }

    public boolean sendCommand(Long deviceId, String command) {
        try {
            Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null || !device.isIotDevice()) {
                return false;
            }

            IotDeviceCommand deviceCommand = new IotDeviceCommand();
            deviceCommand.setDevice(device);
            deviceCommand.setCommand("CUSTOM");
            deviceCommand.setCommandData(command);
            deviceCommand.setStatus("SENT");
            commandRepository.save(deviceCommand);

            // Send command via MQTT
            mqttCommandService.sendCustomCommand(device, command);

            log.info("Custom command sent for device: {}", device.getCode());
            return true;

        } catch (Exception e) {
            log.error("Error sending custom command: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<Object> getCommandHistory(Long deviceId) {
        try {
            List<IotDeviceCommand> commands = commandRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId.toString());
            return commands.stream()
                    .map(cmd -> {
                        Map<String, Object> commandMap = new HashMap<>();
                        commandMap.put("id", cmd.getId());
                        commandMap.put("command", cmd.getCommand());
                        commandMap.put("commandData", cmd.getCommandData());
                        commandMap.put("status", cmd.getStatus());
                        commandMap.put("createdAt", cmd.getCreatedAt());
                        commandMap.put("executedAt", cmd.getExecutedAt());
                        return commandMap;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting command history: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
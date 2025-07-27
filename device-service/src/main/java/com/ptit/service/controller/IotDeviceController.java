package com.ptit.service.controller;

import com.ptit.service.dto.IotDeviceActivationDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.IotSensorData;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.service.IotDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/iot-devices")
@Api(tags = "IoT Device")
@Slf4j
public class IotDeviceController extends BaseController {

    @Autowired
    private IotDeviceService iotDeviceService;

    @GetMapping
    @ApiOperation("Get all IoT devices")
    public ResponseEntity<DataResponse<List<Device>>> getAllIotDevices() {
        List<Device> devices = iotDeviceService.getAllIotDevices();
        return success(devices);
    }

    @GetMapping("/active")
    @ApiOperation("Get active IoT devices (showing on dashboard)")
    public ResponseEntity<DataResponse<List<Device>>> getActiveIotDevices() {
        List<Device> devices = iotDeviceService.getActiveIotDevices();
        return success(devices);
    }

    @GetMapping("/registered")
    @ApiOperation("Get registered IoT devices (not yet activated)")
    public ResponseEntity<DataResponse<List<Device>>> getRegisteredIotDevices() {
        List<Device> devices = iotDeviceService.getRegisteredIotDevices();
        return success(devices);
    }

    @PostMapping("/activate")
    @ApiOperation("Activate IoT device using Active Code")
    public ResponseEntity<DataResponse<MessageResponse>> activateDevice(
            @RequestBody IotDeviceActivationDTO activationDTO) {
        boolean success = iotDeviceService.activateDevice(activationDTO);
        if (success) {
            return success("Device activated successfully");
        } else {
            return ResponseEntity.badRequest().body(DataResponse.badRequest("Failed to activate device"));
        }
    }

    @PostMapping("/qr-scan")
    @ApiOperation("Activate device by scanning QR code")
    public ResponseEntity<DataResponse<MessageResponse>> activateDeviceByQrCode(@RequestParam String qrCodeData) {
        // Extract Active Code from QR code data
        String activeCode = extractActiveCodeFromQrCode(qrCodeData);

        IotDeviceActivationDTO activationDTO = new IotDeviceActivationDTO();
        activationDTO.setActiveCode(activeCode);
        // Set default values for activation
        activationDTO.setDataInterval(30);

        boolean success = iotDeviceService.activateDevice(activationDTO);
        if (success) {
            return success("Device activated successfully via QR code");
        } else {
            return ResponseEntity.badRequest().body(DataResponse.badRequest("Failed to activate device via QR code"));
        }
    }

    @GetMapping("/{deviceId}/data/latest")
    @ApiOperation("Get latest sensor data for device")
    public ResponseEntity<DataResponse<IotSensorData>> getLatestSensorData(@PathVariable Long deviceId) {
        Optional<IotSensorData> sensorData = iotDeviceService.getLatestSensorData(deviceId);
        if (sensorData.isPresent()) {
            return success(sensorData.get());
        } else {
            return ResponseEntity.ok(DataResponse.notFound("Sensor data not found"));
        }
    }

    @GetMapping("/{deviceId}/data/history")
    @ApiOperation("Get sensor data history for device")
    public ResponseEntity<DataResponse<List<IotSensorData>>> getSensorDataHistory(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        List<IotSensorData> sensorData = iotDeviceService.getSensorDataHistory(deviceId, startTime, endTime);
        return success(sensorData);
    }

    @PostMapping("/{deviceId}/deactivate")
    @ApiOperation("Deactivate IoT device")
    public ResponseEntity<DataResponse<MessageResponse>> deactivateDevice(@PathVariable Long deviceId) {
        boolean success = iotDeviceService.deactivateDevice(deviceId);
        if (success) {
            return success("Device deactivated successfully");
        } else {
            return ResponseEntity.badRequest().body(DataResponse.badRequest("Failed to deactivate device"));
        }
    }

    @PostMapping("/{deviceId}/restart")
    @ApiOperation("Restart IoT device")
    public ResponseEntity<DataResponse<MessageResponse>> restartDevice(@PathVariable Long deviceId) {
        boolean success = iotDeviceService.restartDevice(deviceId);
        if (success) {
            return success("Restart command sent successfully");
        } else {
            return ResponseEntity.badRequest().body(DataResponse.badRequest("Failed to send restart command"));
        }
    }

    @PostMapping("/{deviceId}/command")
    @ApiOperation("Send command to IoT device")
    public ResponseEntity<DataResponse<MessageResponse>> sendCommand(
            @PathVariable Long deviceId,
            @RequestBody String command) {
        boolean success = iotDeviceService.sendCommand(deviceId, command);
        if (success) {
            return success("Command sent successfully");
        } else {
            return ResponseEntity.badRequest().body(DataResponse.badRequest("Failed to send command"));
        }
    }

    @GetMapping("/{deviceId}/commands")
    @ApiOperation("Get command history for device")
    public ResponseEntity<DataResponse<List<Object>>> getCommandHistory(@PathVariable Long deviceId) {
        List<Object> commands = iotDeviceService.getCommandHistory(deviceId);
        return success(commands);
    }

    @GetMapping("/dashboard/overview")
    @ApiOperation("Get dashboard overview (only active devices)")
    public ResponseEntity<DataResponse<Object>> getDashboardOverview() {
        List<Device> activeDevices = iotDeviceService.getActiveIotDevices();
        List<Device> registeredDevices = iotDeviceService.getRegisteredIotDevices();

        // Create dashboard overview object
        DashboardOverview overview = new DashboardOverview();
        overview.setTotalDevices(activeDevices.size() + registeredDevices.size());
        overview.setActiveDevices(activeDevices.size());
        overview.setRegisteredDevices(registeredDevices.size());
        overview.setActiveDevicesList(activeDevices);
        overview.setRegisteredDevicesList(registeredDevices);

        return success(overview);
    }

    private String extractActiveCodeFromQrCode(String qrCodeData) {
        // Simple extraction - assuming QR code contains just the Active Code
        // In real implementation, you might need more complex parsing
        return qrCodeData.trim();
    }

    // Inner class for dashboard overview
    public static class DashboardOverview {
        private int totalDevices;
        private int activeDevices;
        private int registeredDevices;
        private List<Device> activeDevicesList;
        private List<Device> registeredDevicesList;

        // Getters and setters
        public int getTotalDevices() {
            return totalDevices;
        }

        public void setTotalDevices(int totalDevices) {
            this.totalDevices = totalDevices;
        }

        public int getActiveDevices() {
            return activeDevices;
        }

        public void setActiveDevices(int activeDevices) {
            this.activeDevices = activeDevices;
        }

        public int getRegisteredDevices() {
            return registeredDevices;
        }

        public void setRegisteredDevices(int registeredDevices) {
            this.registeredDevices = registeredDevices;
        }

        public List<Device> getActiveDevicesList() {
            return activeDevicesList;
        }

        public void setActiveDevicesList(List<Device> activeDevicesList) {
            this.activeDevicesList = activeDevicesList;
        }

        public List<Device> getRegisteredDevicesList() {
            return registeredDevicesList;
        }

        public void setRegisteredDevicesList(List<Device> registeredDevicesList) {
            this.registeredDevicesList = registeredDevicesList;
        }
    }
}
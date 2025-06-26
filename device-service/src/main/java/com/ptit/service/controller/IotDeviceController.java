package com.ptit.service.controller;

import com.ptit.service.dto.IotDeviceActivationDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.IotSensorData;
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
@Api(tags = "IoT Device Management")
@Slf4j
public class IotDeviceController {

    @Autowired
    private IotDeviceService iotDeviceService;

    @GetMapping
    @ApiOperation("Get all IoT devices")
    public ResponseEntity<List<Device>> getAllIotDevices() {
        List<Device> devices = iotDeviceService.getAllIotDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/active")
    @ApiOperation("Get active IoT devices (showing on dashboard)")
    public ResponseEntity<List<Device>> getActiveIotDevices() {
        List<Device> devices = iotDeviceService.getActiveIotDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/registered")
    @ApiOperation("Get registered IoT devices (not yet activated)")
    public ResponseEntity<List<Device>> getRegisteredIotDevices() {
        List<Device> devices = iotDeviceService.getRegisteredIotDevices();
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/activate")
    @ApiOperation("Activate IoT device using Active Code")
    public ResponseEntity<MessageResponse> activateDevice(@RequestBody IotDeviceActivationDTO activationDTO) {
        boolean success = iotDeviceService.activateDevice(activationDTO);
        if (success) {
            return ResponseEntity.ok(new MessageResponse("success", "Device activated successfully"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Failed to activate device"));
        }
    }

    @PostMapping("/qr-scan")
    @ApiOperation("Activate device by scanning QR code")
    public ResponseEntity<MessageResponse> activateDeviceByQrCode(@RequestParam String qrCodeData) {
        // Extract Active Code from QR code data
        String activeCode = extractActiveCodeFromQrCode(qrCodeData);
        
        IotDeviceActivationDTO activationDTO = new IotDeviceActivationDTO();
        activationDTO.setActiveCode(activeCode);
        // Set default values for activation
        activationDTO.setDataInterval(30);
        
        boolean success = iotDeviceService.activateDevice(activationDTO);
        if (success) {
            return ResponseEntity.ok(new MessageResponse("success", "Device activated successfully via QR code"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Failed to activate device via QR code"));
        }
    }

    @GetMapping("/{deviceId}/data/latest")
    @ApiOperation("Get latest sensor data for device")
    public ResponseEntity<IotSensorData> getLatestSensorData(@PathVariable Long deviceId) {
        Optional<IotSensorData> sensorData = iotDeviceService.getLatestSensorData(deviceId);
        return sensorData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{deviceId}/data/history")
    @ApiOperation("Get sensor data history for device")
    public ResponseEntity<List<IotSensorData>> getSensorDataHistory(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<IotSensorData> sensorData = iotDeviceService.getSensorDataHistory(deviceId, startTime, endTime);
        return ResponseEntity.ok(sensorData);
    }

    @PostMapping("/{deviceId}/deactivate")
    @ApiOperation("Deactivate IoT device")
    public ResponseEntity<MessageResponse> deactivateDevice(@PathVariable Long deviceId) {
        boolean success = iotDeviceService.deactivateDevice(deviceId);
        if (success) {
            return ResponseEntity.ok(new MessageResponse("success", "Device deactivated successfully"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Failed to deactivate device"));
        }
    }

    @PostMapping("/{deviceId}/restart")
    @ApiOperation("Restart IoT device")
    public ResponseEntity<MessageResponse> restartDevice(@PathVariable Long deviceId) {
        boolean success = iotDeviceService.restartDevice(deviceId);
        if (success) {
            return ResponseEntity.ok(new MessageResponse("success", "Restart command sent successfully"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Failed to send restart command"));
        }
    }

    @GetMapping("/dashboard/overview")
    @ApiOperation("Get dashboard overview (only active devices)")
    public ResponseEntity<Object> getDashboardOverview() {
        List<Device> activeDevices = iotDeviceService.getActiveIotDevices();
        List<Device> registeredDevices = iotDeviceService.getRegisteredIotDevices();
        
        // Create dashboard overview object
        DashboardOverview overview = new DashboardOverview();
        overview.setTotalDevices(activeDevices.size() + registeredDevices.size());
        overview.setActiveDevices(activeDevices.size());
        overview.setRegisteredDevices(registeredDevices.size());
        overview.setActiveDevicesList(activeDevices);
        overview.setRegisteredDevicesList(registeredDevices);
        
        return ResponseEntity.ok(overview);
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
        public int getTotalDevices() { return totalDevices; }
        public void setTotalDevices(int totalDevices) { this.totalDevices = totalDevices; }
        
        public int getActiveDevices() { return activeDevices; }
        public void setActiveDevices(int activeDevices) { this.activeDevices = activeDevices; }
        
        public int getRegisteredDevices() { return registeredDevices; }
        public void setRegisteredDevices(int registeredDevices) { this.registeredDevices = registeredDevices; }
        
        public List<Device> getActiveDevicesList() { return activeDevicesList; }
        public void setActiveDevicesList(List<Device> activeDevicesList) { this.activeDevicesList = activeDevicesList; }
        
        public List<Device> getRegisteredDevicesList() { return registeredDevicesList; }
        public void setRegisteredDevicesList(List<Device> registeredDevicesList) { this.registeredDevicesList = registeredDevicesList; }
    }
} 
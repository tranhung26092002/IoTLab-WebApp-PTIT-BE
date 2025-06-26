package com.ptit.service.controller;

import com.ptit.service.dto.IotSensorDataDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.IotSensorData;
import com.ptit.service.response.IotDeviceResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.service.IotDeviceService;
import com.ptit.service.service.WebSocketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@RestController
@RequestMapping("/api/iot/realtime")
@Api(tags = "IoT Realtime Data APIs")
@Slf4j
@CrossOrigin(origins = "*")
public class IotRealtimeController {

    @Autowired
    private IotDeviceService iotDeviceService;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * Lấy dữ liệu sensor mới nhất của tất cả thiết bị IoT
     */
    @GetMapping("/latest-sensor-data")
    @ApiOperation("Lấy dữ liệu sensor mới nhất của tất cả thiết bị IoT")
    public ResponseEntity<MessageResponse> getLatestSensorData() {
        try {
            List<Device> activeDevices = iotDeviceService.getActiveIotDevices();
            List<Map<String, Object>> latestData = activeDevices.stream()
                    .map(device -> {
                        Map<String, Object> deviceData = Map.of(
                                "deviceId", device.getCode(),
                                "deviceName", device.getName(),
                                "status", device.getStatus().toString(),
                                "lastSeen", device.getLastSeen()
                        );
                        
                        // Lấy dữ liệu sensor mới nhất
                        iotDeviceService.getLatestSensorData(device.getId()).ifPresent(sensorData -> {
                            deviceData.put("sensorData", Map.of(
                                    "temperature", sensorData.getTemperature(),
                                    "humidity", sensorData.getHumidity(),
                                    "light", sensorData.getLight(),
                                    "timestamp", sensorData.getTimestamp()
                            ));
                        });
                        
                        return deviceData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new MessageResponse("success", "Lấy dữ liệu thành công", latestData));
        } catch (Exception e) {
            log.error("Error getting latest sensor data: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Lỗi khi lấy dữ liệu: " + e.getMessage()));
        }
    }

    /**
     * Lấy dữ liệu sensor mới nhất của một thiết bị cụ thể
     */
    @GetMapping("/device/{deviceId}/latest-sensor-data")
    @ApiOperation("Lấy dữ liệu sensor mới nhất của một thiết bị cụ thể")
    public ResponseEntity<MessageResponse> getLatestSensorDataByDevice(
            @ApiParam("Mã thiết bị") @PathVariable String deviceId) {
        try {
            // Tìm device theo code
            List<Device> devices = iotDeviceService.getAllIotDevices();
            Device device = devices.stream()
                    .filter(d -> d.getCode().equals(deviceId))
                    .findFirst()
                    .orElse(null);

            if (device == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("error", "Không tìm thấy thiết bị"));
            }

            Map<String, Object> response = Map.of(
                    "deviceId", device.getCode(),
                    "deviceName", device.getName(),
                    "status", device.getStatus().toString(),
                    "lastSeen", device.getLastSeen()
            );

            // Lấy dữ liệu sensor mới nhất
            iotDeviceService.getLatestSensorData(device.getId()).ifPresent(sensorData -> {
                response.put("sensorData", Map.of(
                        "temperature", sensorData.getTemperature(),
                        "humidity", sensorData.getHumidity(),
                        "light", sensorData.getLight(),
                        "timestamp", sensorData.getTimestamp()
                ));
            });

            return ResponseEntity.ok(new MessageResponse("success", "Lấy dữ liệu thành công", response));
        } catch (Exception e) {
            log.error("Error getting latest sensor data for device {}: {}", deviceId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Lỗi khi lấy dữ liệu: " + e.getMessage()));
        }
    }

    /**
     * Lấy lịch sử dữ liệu sensor của một thiết bị
     */
    @GetMapping("/device/{deviceId}/sensor-history")
    @ApiOperation("Lấy lịch sử dữ liệu sensor của một thiết bị")
    public ResponseEntity<MessageResponse> getSensorHistory(
            @ApiParam("Mã thiết bị") @PathVariable String deviceId,
            @ApiParam("Thời gian bắt đầu") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @ApiParam("Thời gian kết thúc") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            // Tìm device theo code
            List<Device> devices = iotDeviceService.getAllIotDevices();
            Device device = devices.stream()
                    .filter(d -> d.getCode().equals(deviceId))
                    .findFirst()
                    .orElse(null);

            if (device == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("error", "Không tìm thấy thiết bị"));
            }

            List<IotSensorData> history = iotDeviceService.getSensorDataHistory(device.getId(), startTime, endTime);
            
            List<Map<String, Object>> historyData = history.stream()
                    .map(sensorData -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("timestamp", sensorData.getTimestamp());
                        data.put("temperature", sensorData.getTemperature());
                        data.put("humidity", sensorData.getHumidity());
                        data.put("light", sensorData.getLight());
                        return data;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new MessageResponse("success", "Lấy lịch sử thành công", historyData));
        } catch (Exception e) {
            log.error("Error getting sensor history for device {}: {}", deviceId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Lỗi khi lấy lịch sử: " + e.getMessage()));
        }
    }

    /**
     * Lấy thống kê tổng quan về các thiết bị IoT
     */
    @GetMapping("/dashboard-stats")
    @ApiOperation("Lấy thống kê tổng quan về các thiết bị IoT")
    public ResponseEntity<MessageResponse> getDashboardStats() {
        try {
            List<Device> allDevices = iotDeviceService.getAllIotDevices();
            List<Device> activeDevices = iotDeviceService.getActiveIotDevices();
            List<Device> registeredDevices = iotDeviceService.getRegisteredIotDevices();

            Map<String, Object> stats = Map.of(
                    "totalDevices", allDevices.size(),
                    "activeDevices", activeDevices.size(),
                    "registeredDevices", registeredDevices.size(),
                    "offlineDevices", allDevices.size() - activeDevices.size(),
                    "lastUpdated", LocalDateTime.now()
            );

            return ResponseEntity.ok(new MessageResponse("success", "Lấy thống kê thành công", stats));
        } catch (Exception e) {
            log.error("Error getting dashboard stats: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Lỗi khi lấy thống kê: " + e.getMessage()));
        }
    }

    /**
     * WebSocket endpoint để subscribe vào dữ liệu realtime
     */
    @MessageMapping("/subscribe-device")
    @SendTo("/iot/device-data")
    public Map<String, Object> subscribeToDevice(String deviceId) {
        return Map.of(
                "deviceId", deviceId,
                "message", "Subscribed to device data",
                "timestamp", LocalDateTime.now()
        );
    }

    /**
     * Test endpoint để gửi dữ liệu test qua WebSocket
     */
    @PostMapping("/test-websocket")
    @ApiOperation("Test gửi dữ liệu qua WebSocket")
    public ResponseEntity<MessageResponse> testWebSocket(
            @ApiParam("Mã thiết bị") @RequestParam String deviceId,
            @ApiParam("Nhiệt độ") @RequestParam Double temperature,
            @ApiParam("Độ ẩm") @RequestParam Double humidity) {
        try {
            // Tạo dữ liệu test
            IotSensorData testData = new IotSensorData();
            testData.setDeviceId(deviceId);
            testData.setTemperature(temperature);
            testData.setHumidity(humidity);
            testData.setTimestamp(LocalDateTime.now());

            // Gửi qua WebSocket
            webSocketService.sendSensorData(testData);

            return ResponseEntity.ok(new MessageResponse("success", "Dữ liệu test đã được gửi qua WebSocket"));
        } catch (Exception e) {
            log.error("Error testing WebSocket: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new MessageResponse("error", "Lỗi khi test WebSocket: " + e.getMessage()));
        }
    }
} 
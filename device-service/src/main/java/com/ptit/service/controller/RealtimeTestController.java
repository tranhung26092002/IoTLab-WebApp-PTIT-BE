package com.ptit.service.controller;

import com.ptit.service.response.MessageResponse;
import com.ptit.service.service.RealtimeNotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test/realtime")
@Api(tags = "Realtime WebSocket Test", description = "Test gửi dữ liệu realtime tới dashboard qua WebSocket")
@RequiredArgsConstructor
public class RealtimeTestController extends BaseController {
    private final RealtimeNotificationService realtimeNotificationService;

    @PostMapping("/sensor-data/{deviceId}")
    @ApiOperation("Test gửi dữ liệu cảm biến realtime")
    public ResponseEntity<MessageResponse> testSendSensorData(@PathVariable String deviceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("temperature", 25.5);
        data.put("humidity", 60.2);
        data.put("gas", 450);
        data.put("timestamp", System.currentTimeMillis());
        realtimeNotificationService.sendSensorData(deviceId, data);
        return ResponseEntity.ok(
                MessageResponse.builder().message("Đã gửi dữ liệu cảm biến realtime cho deviceId=" + deviceId).build());
    }

    @PostMapping("/status/{deviceId}")
    @ApiOperation("Test gửi trạng thái thiết bị realtime")
    public ResponseEntity<MessageResponse> testSendDeviceStatus(@PathVariable String deviceId) {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "ACTIVE");
        status.put("lastSeen", System.currentTimeMillis());
        realtimeNotificationService.sendDeviceStatus(deviceId, status);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Đã gửi trạng thái thiết bị realtime cho deviceId=" + deviceId).build());
    }

    @PostMapping("/command/{deviceId}")
    @ApiOperation("Test gửi kết quả lệnh điều khiển realtime")
    public ResponseEntity<MessageResponse> testSendCommandResult(@PathVariable String deviceId) {
        Map<String, Object> command = new HashMap<>();
        command.put("command", "LED_CONTROL");
        command.put("result", "SUCCESS");
        command.put("executedAt", System.currentTimeMillis());
        realtimeNotificationService.sendCommandResult(deviceId, command);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Đã gửi kết quả lệnh điều khiển realtime cho deviceId=" + deviceId).build());
    }

    @PostMapping("/alert")
    @ApiOperation("Test gửi cảnh báo realtime")
    public ResponseEntity<MessageResponse> testSendAlert() {
        Map<String, Object> alert = new HashMap<>();
        alert.put("type", "GAS");
        alert.put("message", "Phát hiện khí gas vượt ngưỡng!");
        alert.put("level", "WARNING");
        alert.put("timestamp", System.currentTimeMillis());
        realtimeNotificationService.sendAlert(alert);
        return ResponseEntity.ok(MessageResponse.builder().message("Đã gửi cảnh báo realtime").build());
    }
}
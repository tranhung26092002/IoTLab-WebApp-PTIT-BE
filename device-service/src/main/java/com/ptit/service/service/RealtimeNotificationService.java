package com.ptit.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi dữ liệu cảm biến mới tới dashboard
     */
    public void sendSensorData(String deviceId, Object payload) {
        String topic = "/topic/iot/devices/" + deviceId + "/data";
        messagingTemplate.convertAndSend(topic, payload);
        log.info("[WebSocket] Đã gửi dữ liệu cảm biến tới {}: {}", topic, payload);
    }

    /**
     * Gửi trạng thái thiết bị
     */
    public void sendDeviceStatus(String deviceId, Object payload) {
        String topic = "/topic/iot/devices/" + deviceId + "/status";
        messagingTemplate.convertAndSend(topic, payload);
        log.info("[WebSocket] Đã gửi trạng thái thiết bị tới {}: {}", topic, payload);
    }

    /**
     * Gửi kết quả lệnh điều khiển
     */
    public void sendCommandResult(String deviceId, Object payload) {
        String topic = "/topic/iot/devices/" + deviceId + "/command";
        messagingTemplate.convertAndSend(topic, payload);
        log.info("[WebSocket] Đã gửi kết quả lệnh tới {}: {}", topic, payload);
    }

    /**
     * Gửi cảnh báo chung
     */
    public void sendAlert(Object payload) {
        String topic = "/topic/iot/alerts";
        messagingTemplate.convertAndSend(topic, payload);
        log.info("[WebSocket] Đã gửi cảnh báo tới {}: {}", topic, payload);
    }
} 
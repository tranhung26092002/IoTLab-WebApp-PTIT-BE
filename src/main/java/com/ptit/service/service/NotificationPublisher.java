package com.ptit.service.service;

import com.ptit.service.dto.NotificationDTO;
import com.ptit.service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishNotification(Long userId, Long deviceId, String deviceName, String message, String expiredAt, String type) {
        NotificationDTO notification = new NotificationDTO(userId, deviceId, deviceName, message, expiredAt, type);

        try {
            // Gửi thông báo qua RabbitMQ
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                    notification
            );
            System.out.println("Notification sent successfully: " + notification);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}


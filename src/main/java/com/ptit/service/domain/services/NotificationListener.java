package com.ptit.service.domain.services;

import com.ptit.service.app.controllers.WebSocketController;
import com.ptit.service.app.dtos.NotificationDTO;
import com.ptit.service.config.RabbitMQConfig;
import com.ptit.service.domain.entities.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;
    private final WebSocketController webSocketController;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationDTO notification) {
        try {
            // In ra thông báo nhận được từ RabbitMQ
            System.out.println("Received notification: " + notification);

            // Lưu thông báo vào cơ sở dữ liệu
            Notification newNotification = notificationService.saveNotification(notification);
//
//            // Gửi thông báo đến các client qua WebSocket
//            webSocketController.sendNotification(newNotification);

        } catch (Exception e) {
            System.err.println("Failed to process notification: " + e.getMessage());
            // Thêm logic xử lý lỗi nếu cần (ví dụ: lưu thông báo lỗi vào DB hoặc gửi cảnh báo)
        }
    }
}


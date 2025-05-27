package com.ptit.service.controller;

import com.ptit.service.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(Notification notification) {
        try {
            // Gửi đến topic của người dùng cụ thể (userId)
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + notification.getUserId(),
                    notification
            );

            // Gửi đến topic chung cho tất cả người dùng
            messagingTemplate.convertAndSend(
                    "/topic/notifications",
                    notification
            );

            System.out.println("Notification sent to user " + notification.getUserId() + " and general topic.");
        } catch (Exception e) {
            // Log lỗi nếu có vấn đề trong việc gửi thông báo
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}

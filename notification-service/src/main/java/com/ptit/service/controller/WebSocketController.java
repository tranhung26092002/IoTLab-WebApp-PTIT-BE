package com.ptit.service.controller;

import com.ptit.service.entity.Notification;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "WebSocket", description = "APIs WebSocket cho thông báo thời gian thực")
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @ApiOperation(value = "Gửi thông báo qua WebSocket", notes = "Gửi thông báo đến người dùng cụ thể và topic chung")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Gửi thành công"),
        @ApiResponse(code = 500, message = "Lỗi gửi thông báo")
    })
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

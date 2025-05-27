package com.ptit.service.service;

import com.ptit.service.dto.NotificationDTO;
import com.ptit.service.config.RabbitMQConfig;
import com.ptit.service.entity.User;
import com.ptit.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class UserListener {

    private final EmailService emailService;
    private final UserRepository userRepository;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationDTO notification) {
        try {
            // Tìm user bằng userId
            User user = userRepository.findById(notification.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + notification.getUserId()));

            // Lấy email từ user
            String recipientEmail = user.getEmail();

            // Tạo context cho nội dung email
            Context context = new Context();
            context.setVariable("title", notification.getTitle());
            context.setVariable("message", notification.getMessage());
            context.setVariable("expiredAt", notification.getExpiredAt());
            context.setVariable("recipientName", user.getFullName());

            // Gửi email thông báo
            emailService.sendEmail(
                    recipientEmail,                 // Email người nhận
                    notification.getTitle(),        // Tiêu đề email
                    "notification-template",        // Tên template Thymeleaf
                    context                         // Context chứa nội dung email
            );

            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (Exception e) {
            System.err.println("Failed to process notification: " + e.getMessage());
            // Thêm logic xử lý lỗi nếu cần (ví dụ: lưu thông báo lỗi vào DB hoặc gửi cảnh báo)
        }
    }
}

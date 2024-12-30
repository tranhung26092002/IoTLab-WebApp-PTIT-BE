package com.ptit.service.domain.schedulers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.app.dtos.NotificationDTO;
import com.ptit.service.domain.entities.BorrowRecord;
import com.ptit.service.domain.services.BorrowRecordService;
import com.ptit.service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceReturnScheduler {

    private final BorrowRecordService borrowRecordService;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0 0/5 * * * ?") // Run every 5 minutes
    public void checkDeviceReturns() {
        List<BorrowRecord> devicesToReturn = borrowRecordService.findDevicesDueForReturn();

        if (devicesToReturn.isEmpty()) {
            System.out.println("No devices due for return today.");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper for JSON conversion

        devicesToReturn.forEach(borrowRecord -> {
            NotificationDTO notification = new NotificationDTO(
                    borrowRecord.getUserId(),
                    borrowRecord.getId(),
                    borrowRecord.getDevice().getName(),
                    "Your device " + borrowRecord.getDevice().getName() +
                            " is due for return on " + borrowRecord.getExpiredAt(),
                    borrowRecord.getExpiredAt().toString(),
                    "DEVICE_RETURN"
            );

            try {
                // Chuyển NotificationDTO thành chuỗi JSON
                String notificationJson = objectMapper.writeValueAsString(notification);

                // Tạo Message với kiểu nội dung là JSON
                MessageProperties messageProperties = new MessageProperties();
                messageProperties.setContentType("application/json");
                Message message = new Message(notificationJson.getBytes(), messageProperties);

                // Gửi Message qua RabbitMQ
                rabbitTemplate.send(
                        RabbitMQConfig.NOTIFICATION_EXCHANGE,
                        RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                        message
                );

                System.out.println("Notification sent for device: " + borrowRecord.getDevice().getName());
            } catch (Exception e) {
                System.err.println("Failed to send notification for device: " + borrowRecord.getDevice().getName() + " - " + e.getMessage());
            }
        });
    }
}
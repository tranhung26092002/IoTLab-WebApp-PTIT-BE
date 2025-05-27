package com.ptit.service.service;

import com.ptit.service.dto.NotificationDTO;
import com.ptit.service.response.NotificationResponse;
import com.ptit.service.entity.Notification;
import com.ptit.service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification saveNotification(NotificationDTO notificationDTO) {
        notificationRepository.findByUserIdAndBorrowRecordId(
                notificationDTO.getUserId(),
                notificationDTO.getBorrowRecordId()
        ).ifPresent(notification -> {
            // Nếu thông báo đã tồn tại thì không lưu thông báo mới
            throw new RuntimeException("Notification already exists");
        });

        // Tạo đối tượng Notification từ NotificationDTO
        Notification notification = new Notification();
        notification.setUserId(notificationDTO.getUserId());
        notification.setBorrowRecordId(notificationDTO.getBorrowRecordId());
        notification.setTitle("Thông báo từ thiết bị " + notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(notificationDTO.getType());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        // Lưu vào cơ sở dữ liệu
        return notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        return notificationRepository.findAllByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
        return convertToDTO(notification);
    }

    @Transactional
    public List<NotificationResponse> markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }


    @Transactional
    public void deleteAllNotifications(Long userId) {
        notificationRepository.deleteAllNotificationsByUserId(userId);
    }

    private NotificationResponse convertToDTO(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getBorrowRecordId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isRead(),
                notification.getType()
        );
    }
}

package com.ptit.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptit.service.entity.Notification;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Notification> findByUserIdAndBorrowRecordId(Long userId, Long borrowRecordId);

    List<Notification> findAllByUserId(Long userId);

    void deleteAllNotificationsByUserId(Long userId);
}

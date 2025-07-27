package com.ptit.service.repository;

import com.ptit.service.entity.IotDeviceCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IotDeviceCommandRepository extends JpaRepository<IotDeviceCommand, Long> {

    // Tìm commands theo device
    List<IotDeviceCommand> findByDeviceIdOrderByCreatedAtDesc(String deviceId);

    // Tìm commands theo device với pagination
    Page<IotDeviceCommand> findByDeviceId(String deviceId, Pageable pageable);

    // Tìm commands theo status
    List<IotDeviceCommand> findByStatus(String status);

    // Tìm commands theo device và status
    List<IotDeviceCommand> findByDeviceIdAndStatus(String deviceId, String status);

    // Tìm commands pending (chưa được gửi)
    List<IotDeviceCommand> findByStatusOrderByCreatedAtAsc(String status);

    // Tìm commands được tạo trong khoảng thời gian
    @Query("SELECT c FROM IotDeviceCommand c WHERE c.createdAt BETWEEN :startTime AND :endTime")
    List<IotDeviceCommand> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // Tìm commands theo device và loại command
    List<IotDeviceCommand> findByDeviceIdAndCommandOrderByCreatedAtDesc(String deviceId, String command);

    // Tìm commands được gửi bởi user
    List<IotDeviceCommand> findBySentByOrderByCreatedAtDesc(Long sentBy);

    // Đếm số commands theo status
    @Query("SELECT COUNT(c) FROM IotDeviceCommand c WHERE c.status = :status")
    Long countByStatus(@Param("status") String status);

    // Đếm số commands theo device và status
    @Query("SELECT COUNT(c) FROM IotDeviceCommand c WHERE c.deviceId = :deviceId AND c.status = :status")
    Long countByDeviceIdAndStatus(@Param("deviceId") String deviceId, @Param("status") String status);
}
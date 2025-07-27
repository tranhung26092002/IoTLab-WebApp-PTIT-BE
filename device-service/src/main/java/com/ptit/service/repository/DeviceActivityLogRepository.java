package com.ptit.service.repository;

import com.ptit.service.entity.DeviceActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeviceActivityLogRepository extends JpaRepository<DeviceActivityLog, Long> {

    // Tìm logs theo device
    List<DeviceActivityLog> findByDeviceIdOrderByTimestampDesc(String deviceId);

    // Tìm logs theo device với pagination
    Page<DeviceActivityLog> findByDeviceId(String deviceId, Pageable pageable);

    // Tìm logs theo activity type
    List<DeviceActivityLog> findByActivityTypeOrderByTimestampDesc(String activityType);

    // Tìm logs theo device và activity type
    List<DeviceActivityLog> findByDeviceIdAndActivityTypeOrderByTimestampDesc(String deviceId, String activityType);

    // Tìm logs theo severity
    List<DeviceActivityLog> findBySeverityOrderByTimestampDesc(String severity);

    // Tìm logs theo source
    List<DeviceActivityLog> findBySourceOrderByTimestampDesc(String source);

    // Tìm logs theo user
    List<DeviceActivityLog> findByUserIdOrderByTimestampDesc(Long userId);

    // Tìm logs trong khoảng thời gian
    @Query("SELECT l FROM DeviceActivityLog l WHERE l.timestamp BETWEEN :startTime AND :endTime")
    List<DeviceActivityLog> findByTimestampBetween(@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // Tìm logs theo device và khoảng thời gian
    @Query("SELECT l FROM DeviceActivityLog l WHERE l.deviceId = :deviceId AND l.timestamp BETWEEN :startTime AND :endTime")
    List<DeviceActivityLog> findByDeviceIdAndTimestampBetween(@Param("deviceId") String deviceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // Tìm logs lỗi
    List<DeviceActivityLog> findBySeverityInOrderByTimestampDesc(List<String> severities);

    // Tìm logs lỗi theo device
    List<DeviceActivityLog> findByDeviceIdAndSeverityInOrderByTimestampDesc(String deviceId, List<String> severities);

    // Đếm logs theo activity type
    @Query("SELECT COUNT(l) FROM DeviceActivityLog l WHERE l.activityType = :activityType")
    Long countByActivityType(@Param("activityType") String activityType);

    // Đếm logs theo device và activity type
    @Query("SELECT COUNT(l) FROM DeviceActivityLog l WHERE l.deviceId = :deviceId AND l.activityType = :activityType")
    Long countByDeviceIdAndActivityType(@Param("deviceId") String deviceId, @Param("activityType") String activityType);

    // Tìm logs gần đây nhất
    @Query("SELECT l FROM DeviceActivityLog l WHERE l.deviceId = :deviceId ORDER BY l.timestamp DESC")
    List<DeviceActivityLog> findRecentByDeviceId(@Param("deviceId") String deviceId, Pageable pageable);
}
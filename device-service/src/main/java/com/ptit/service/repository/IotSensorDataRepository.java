package com.ptit.service.repository;

import com.ptit.service.entity.IotSensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IotSensorDataRepository extends JpaRepository<IotSensorData, Long> {
    
    List<IotSensorData> findByDeviceIdOrderByTimestampDesc(Long deviceId);
    
    @Query("SELECT i FROM IotSensorData i WHERE i.device.id = :deviceId AND i.timestamp >= :startTime ORDER BY i.timestamp DESC")
    List<IotSensorData> findByDeviceIdAndTimestampAfterOrderByTimestampDesc(
            @Param("deviceId") Long deviceId, 
            @Param("startTime") LocalDateTime startTime);
    
    // Sử dụng method naming convention thay vì JPQL với LIMIT
    Optional<IotSensorData> findFirstByDeviceIdOrderByTimestampDesc(Long deviceId);
    
    @Query("SELECT i FROM IotSensorData i WHERE i.device.id = :deviceId AND i.timestamp BETWEEN :startTime AND :endTime ORDER BY i.timestamp ASC")
    List<IotSensorData> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
            @Param("deviceId") Long deviceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
} 
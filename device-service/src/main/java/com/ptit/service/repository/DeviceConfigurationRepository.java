package com.ptit.service.repository;

import com.ptit.service.entity.DeviceConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceConfigurationRepository extends JpaRepository<DeviceConfiguration, Long> {

    // Tìm cấu hình hiện tại của device
    Optional<DeviceConfiguration> findByDeviceIdAndIsActiveTrue(String deviceId);

    // Tìm tất cả cấu hình của device
    List<DeviceConfiguration> findByDeviceIdOrderByCreatedAtDesc(String deviceId);

    // Tìm cấu hình theo version
    Optional<DeviceConfiguration> findByDeviceIdAndConfigVersion(String deviceId, String configVersion);

    // Tìm cấu hình được áp dụng bởi user
    List<DeviceConfiguration> findByAppliedByOrderByAppliedAtDesc(Long appliedBy);

    // Tìm cấu hình active
    List<DeviceConfiguration> findByIsActiveTrue();

    // Kiểm tra device có cấu hình active không
    @Query("SELECT COUNT(c) > 0 FROM DeviceConfiguration c WHERE c.deviceId = :deviceId AND c.isActive = true")
    boolean existsByDeviceIdAndIsActiveTrue(@Param("deviceId") String deviceId);

    // Tìm cấu hình theo data interval
    List<DeviceConfiguration> findByDataInterval(Integer dataInterval);

    // Tìm cấu hình có gas threshold
    List<DeviceConfiguration> findByGasThresholdGreaterThan(Integer gasThreshold);
}
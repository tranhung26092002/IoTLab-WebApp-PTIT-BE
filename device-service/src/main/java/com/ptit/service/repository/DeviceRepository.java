package com.ptit.service.repository;

import com.ptit.service.dto.DeviceFilterDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.enums.DeviceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
        Optional<Device> findByIdAndStatus(Long id, DeviceStatus status);

        @Query("SELECT d FROM Device d WHERE " +
                        "(:#{#filter.name} IS NULL OR d.name LIKE %:#{#filter.name}%) AND " +
                        "(:#{#filter.type} IS NULL OR d.type LIKE %:#{#filter.type}%) AND " +
                        "(:#{#filter.status} IS NULL OR d.status = :#{#filter.status})")
        Page<Device> filterDevices(
                        @Param("filter") DeviceFilterDTO deviceFilterDto,
                        Pageable pageable);

        Optional<Device> findByCode(String code);

        // IoT Device specific queries
        Optional<Device> findByActiveCode(String activeCode);

        Optional<Device> findByMacAddress(String macAddress);

        List<Device> findByIsIotDeviceTrue();

        List<Device> findByIsIotDeviceTrueAndStatus(DeviceStatus status);

        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.status IN ('REGISTERED', 'ACTIVE', 'OFFLINE', 'ERROR')")
        List<Device> findAllIotDevices();

        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.status = 'ACTIVE'")
        List<Device> findActiveIotDevices();

        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.status = 'REGISTERED'")
        List<Device> findRegisteredIotDevices();

        @Query("SELECT d FROM Device d WHERE d.isIotDevice = false")
        List<Device> findRegularDevices();

        // IoT Device specific queries - cải tiến
        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.iotStatus = :iotStatus")
        List<Device> findIotDevicesByStatus(@Param("iotStatus") com.ptit.service.entity.enums.DeviceIotStatus iotStatus);

        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.physicalStatus = :physicalStatus")
        List<Device> findIotDevicesByPhysicalStatus(@Param("physicalStatus") com.ptit.service.entity.enums.DevicePhysicalStatus physicalStatus);

        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.deviceCategory = :category")
        List<Device> findIotDevicesByCategory(@Param("category") String category);

        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.difficultyLevel = :level")
        List<Device> findIotDevicesByDifficultyLevel(@Param("level") String level);

        // Tìm thiết bị theo sensors
        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.sensors LIKE %:sensor%")
        List<Device> findIotDevicesBySensor(@Param("sensor") String sensor);

        // Tìm thiết bị theo capabilities
        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.capabilities LIKE %:capability%")
        List<Device> findIotDevicesByCapability(@Param("capability") String capability);

        // Tìm thiết bị offline (không gửi data trong 5 phút)
        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND (d.lastSeen IS NULL OR d.lastSeen < :offlineThreshold)")
        List<Device> findOfflineIotDevices(@Param("offlineThreshold") LocalDateTime offlineThreshold);

        // Tìm thiết bị có battery thấp
        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.batteryLevel < :batteryThreshold")
        List<Device> findIotDevicesWithLowBattery(@Param("batteryThreshold") Integer batteryThreshold);

        // Tìm thiết bị theo firmware version
        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.firmwareVersion = :firmwareVersion")
        List<Device> findIotDevicesByFirmwareVersion(@Param("firmwareVersion") String firmwareVersion);

        // Tìm thiết bị theo WiFi SSID
        @Query("SELECT d FROM Device d WHERE d.isIotDevice = true AND d.wifiSsid = :wifiSsid")
        List<Device> findIotDevicesByWifiSsid(@Param("wifiSsid") String wifiSsid);
}

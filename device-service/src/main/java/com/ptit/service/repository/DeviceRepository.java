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
            Pageable pageable
    );

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
}

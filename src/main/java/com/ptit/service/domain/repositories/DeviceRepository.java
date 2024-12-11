package com.ptit.service.domain.repositories;

import com.ptit.service.app.dtos.DeviceFilterDto;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.enums.DeviceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByIdAndStatus(Long id, DeviceStatus status);

    @Query("SELECT d FROM Device d WHERE " +
            "(:#{#filter.name} IS NULL OR d.name LIKE %:#{#filter.name}%) AND " +
            "(:#{#filter.type} IS NULL OR d.type LIKE %:#{#filter.type}%) AND " +
            "(:#{#filter.status} IS NULL OR d.status = :#{#filter.status})")
    Page<Device> filterDevices(
            @Param("filter") DeviceFilterDto deviceFilterDto,
            Pageable pageable
    );

    Optional<Device> findByCode(String code);
}

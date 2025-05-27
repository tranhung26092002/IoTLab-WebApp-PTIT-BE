package com.ptit.service.repository;

import com.ptit.service.entity.SensorData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    SensorData findFirstByDeviceIdOrderByCreatedAtDesc(Long deviceId);

    Page<SensorData> findTop100ByDeviceIdOrderByCreatedAtDesc(Long deviceId, Pageable pageable);
}

package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.SensorData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    SensorData findFirstByDeviceIdOrderByCreatedAtDesc(Long deviceId);

    Page<SensorData> findTop100ByDeviceIdOrderByCreatedAtDesc(Long deviceId, Pageable pageable);
}

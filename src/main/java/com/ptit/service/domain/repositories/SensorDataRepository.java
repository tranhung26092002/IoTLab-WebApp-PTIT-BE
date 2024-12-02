package com.ptit.service.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptit.service.domain.entities.SensorData;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findByNodeId(String nodeId);

    List<SensorData> findAllByNodeId(Long nodeId);

    SensorData findTopByOrderByIdDesc();

        Optional<SensorData> findTopByNodeIdOrderByTimestampDesc(Long nodeId);
    
    List<SensorData> findByNodeIdAndTimestampAfterOrderByTimestampDesc(
        Long nodeId, 
        LocalDateTime timestamp
    );
}

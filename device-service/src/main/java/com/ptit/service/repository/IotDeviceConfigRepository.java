package com.ptit.service.repository;

import com.ptit.service.entity.IotDeviceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IotDeviceConfigRepository extends JpaRepository<IotDeviceConfig, Long> {
    
    Optional<IotDeviceConfig> findByDeviceId(Long deviceId);
} 
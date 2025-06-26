package com.ptit.service.repository;

import com.ptit.service.entity.IotDeviceCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IotDeviceCommandRepository extends JpaRepository<IotDeviceCommand, Long> {
    
    List<IotDeviceCommand> findByDeviceIdOrderByCreatedAtDesc(Long deviceId);
    
    List<IotDeviceCommand> findByDeviceIdAndStatusOrderByCreatedAtDesc(Long deviceId, String status);
} 
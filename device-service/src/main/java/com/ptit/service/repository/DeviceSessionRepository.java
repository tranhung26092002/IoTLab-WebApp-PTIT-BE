package com.ptit.service.repository;

import com.ptit.service.entity.DeviceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceSessionRepository extends JpaRepository<DeviceSession, Long> {
}
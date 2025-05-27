package com.ptit.service.repository;

import com.ptit.service.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByUserIdAndShiftAndCheckInTimeBetween(Long userId, String shift, LocalDateTime checkInTimeAfter, LocalDateTime checkInTimeAfter1);

    Page<Attendance> findByCheckInTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
}

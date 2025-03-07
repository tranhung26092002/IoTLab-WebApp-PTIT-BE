package com.ptit.service.domain.repositories;

import com.ptit.service.app.responses.AttendanceResponse;
import com.ptit.service.domain.entities.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByUserIdAndShift(Long userId, String shift);
}

package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE " +
            "EXISTS (SELECT s FROM r.students s WHERE s.userId = :studentId)")
    Page<Report> findByStudentsId(Long studentId, Pageable pageable);
}

package com.ptit.service.domain.repositories;

import com.ptit.service.app.dtos.ReportFilterDTO;
import com.ptit.service.domain.entities.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE " +
            "EXISTS (SELECT s FROM r.students s WHERE s.userId = :studentId)")
    Page<Report> findByStudentsId(Long studentId, Pageable pageable);

    @Query("SELECT r FROM Report r WHERE " +
            "(:#{#filter.id} IS NULL OR r.id = :#{#filter.id}) AND " +
            "(:#{#filter.title} IS NULL OR r.title LIKE %:#{#filter.title}%) AND " +
            "(:#{#filter.className} IS NULL OR r.className LIKE %:#{#filter.className}%) AND " +
            "(:#{#filter.classGroup} IS NULL OR r.classGroup LIKE %:#{#filter.classGroup}%) AND " +
            "(:#{#filter.shift} IS NULL OR r.shift = :#{#filter.shift}) AND " +
            "(:#{#filter.status} IS NULL OR r.status = :#{#filter.status}) AND" +
            "(:#{#filter.startDate} IS NULL OR r.createdAt >= :#{#filter.startDate}) AND " +
            "(:#{#filter.endDate} IS NULL OR r.createdAt <= :#{#filter.endDate})"
    )
    Page<Report> filterDevices(
            @Param("filter") ReportFilterDTO reportFilterDTO,
            Pageable pageRequest);
}

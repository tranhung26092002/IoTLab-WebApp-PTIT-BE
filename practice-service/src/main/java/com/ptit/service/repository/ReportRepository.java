package com.ptit.service.repository;

import com.ptit.service.dto.ReportFilterDTO;
import com.ptit.service.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
        @Query("SELECT r FROM Report r WHERE " +
                        "EXISTS (SELECT s FROM r.students s WHERE s.id = :studentId)")
        Page<Report> findByStudentsId(Long studentId, Pageable pageable);

        @Query("SELECT r FROM Report r WHERE " +
                        "(:#{#filter.id} IS NULL OR r.id = :#{#filter.id}) AND " +
                        "(:#{#filter.studentId} IS NULL OR EXISTS (SELECT s FROM r.students s WHERE s.id = :#{#filter.studentId})) AND " +
                        "(:#{#filter.title} IS NULL OR r.title LIKE %:#{#filter.title}%) AND " +
                        "(:#{#filter.className} IS NULL OR r.className LIKE %:#{#filter.className}%) AND " +
                        "(:#{#filter.classGroup} IS NULL OR r.classGroup LIKE %:#{#filter.classGroup}%) AND " +
                        "(:#{#filter.shift} IS NULL OR r.shift = :#{#filter.shift}) AND " +
                        "(:#{#filter.status} IS NULL OR r.status = :#{#filter.status}) AND" +
                        "(:#{#filter.startDate} IS NULL OR r.createdAt >= :#{#filter.startDate}) AND " +
                        "(:#{#filter.endDate} IS NULL OR r.createdAt <= :#{#filter.endDate})")
        Page<Report> filterReports(
                        @Param("filter") ReportFilterDTO reportFilterDTO,
                        Pageable pageRequest);

        List<Report> findByIsDeletedFalse();

        @Query("SELECT DISTINCT r.practice.id FROM Report r JOIN r.students s WHERE s.id = :studentId AND r.isDeleted = false")
        List<Long> findPracticeIdsByStudentId(@Param("studentId") Long studentId);

        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Report r JOIN r.students s WHERE s.id = :studentId AND r.practice.id = :practiceId AND r.isDeleted = false")
        boolean existsByStudentIdAndPracticeId(@Param("studentId") Long studentId,
                        @Param("practiceId") Long practiceId);
}

package com.ptit.service.repository;

import com.ptit.service.dto.PraticeFilterDTO;
import com.ptit.service.entity.Practice;
import com.ptit.service.entity.enums.PracticeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {
        List<Practice> findAllByOrderByPracticeOrderAsc();

        @Query("SELECT p FROM Practice p WHERE p.status = ?1 ORDER BY p.id ASC")
        Page<Practice> search(PracticeStatus status, Pageable pageable);

        @Query("SELECT p FROM Practice p WHERE " +
                        "(:#{#filter.id} IS NULL OR p.id = :#{#filter.id}) AND " +
                        "(:#{#filter.title} IS NULL OR p.title LIKE %:#{#filter.title}%) AND " +
                        "(:#{#filter.status} IS NULL OR p.status = :#{#filter.status}) ")
        Page<Practice> getPracticeFilter(
                        @Param("filter") PraticeFilterDTO praticeFilterDTO,
                        Pageable pageRequest);

        @Query("SELECT p FROM Practice p WHERE p.id NOT IN " +
                        "(SELECT sp.practice.id FROM StudentProgress sp WHERE sp.student.id = :studentId) " +
                        "AND p.id > " +
                        "(SELECT COALESCE(MAX(sp2.practice.id), 0) FROM StudentProgress sp2 " +
                        "WHERE sp2.student.id = :studentId AND sp2.status = 'COMPLETED') " +
                        "ORDER BY p.practiceOrder ASC")
        List<Practice> findNextUnlockedPractice(@Param("studentId") Long studentId);

        Optional<Practice> findTopByPracticeOrderLessThanOrderByPracticeOrderDesc(
                        Integer practiceOrder);

        Optional<Practice> findByPracticeOrder(Integer practiceOrder);

        @Query("SELECT COALESCE(MAX(p.practiceOrder), 0) FROM Practice p")
        Integer findMaxPracticeOrder();
}

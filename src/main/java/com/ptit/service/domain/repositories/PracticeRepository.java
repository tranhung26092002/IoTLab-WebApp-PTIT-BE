package com.ptit.service.domain.repositories;

import com.ptit.service.app.dtos.PraticeFilterDTO;
import com.ptit.service.domain.entities.Practice;
import com.ptit.service.domain.enums.PracticeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {

    @Query("SELECT p FROM Practice p WHERE p.status = ?1 ORDER BY p.id ASC")
    Page<Practice> search(PracticeStatus status, Pageable pageable);

    @Query("SELECT p FROM Practice p WHERE " +
            "(:#{#filter.id} IS NULL OR p.id = :#{#filter.id}) AND " +
            "(:#{#filter.title} IS NULL OR p.title LIKE %:#{#filter.title}%) AND " +
            "(:#{#filter.status} IS NULL OR p.status = :#{#filter.status}) "
    )
    Page<Practice> getPracticeFilter(
            @Param("filter") PraticeFilterDTO praticeFilterDTO,
            Pageable pageRequest);
}

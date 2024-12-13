package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.Practice;
import com.ptit.service.domain.enums.PracticeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {

    @Query("SELECT p FROM Practice p WHERE p.status = ?1 ORDER BY p.id ASC")
    Page<Practice> search(PracticeStatus status, Pageable pageable);
}

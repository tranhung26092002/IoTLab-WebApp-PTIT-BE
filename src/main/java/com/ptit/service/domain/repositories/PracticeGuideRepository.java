package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.PracticeGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeGuideRepository extends JpaRepository<PracticeGuide, Long> {
    List<PracticeGuide> findAllByPracticeIdOrderByIdAsc(Long id);
}

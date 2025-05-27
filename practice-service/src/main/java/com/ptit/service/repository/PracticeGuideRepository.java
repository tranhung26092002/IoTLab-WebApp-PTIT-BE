package com.ptit.service.repository;

import com.ptit.service.entity.PracticeGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeGuideRepository extends JpaRepository<PracticeGuide, Long> {
    List<PracticeGuide> findAllByPracticeIdOrderByIdAsc(Long id);
}

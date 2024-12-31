package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.PracticeVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeVideoRepository extends JpaRepository<PracticeVideo, Long> {

    List<PracticeVideo> findAllByPracticeIdOrderByIdAsc(Long id);
}

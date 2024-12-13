package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.PracticeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeFileRepository extends JpaRepository<PracticeFile, Long> {
    List<PracticeFile> findAllByPracticeIdOrderByIdAsc(Long id);
}

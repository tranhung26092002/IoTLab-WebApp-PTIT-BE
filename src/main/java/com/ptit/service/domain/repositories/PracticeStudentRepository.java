package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.PracticeStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeStudentRepository extends JpaRepository<PracticeStudent, Long> {
}

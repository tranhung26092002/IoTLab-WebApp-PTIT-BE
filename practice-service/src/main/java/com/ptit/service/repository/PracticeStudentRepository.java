package com.ptit.service.repository;

import com.ptit.service.entity.PracticeStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeStudentRepository extends JpaRepository<PracticeStudent, Long> {
}

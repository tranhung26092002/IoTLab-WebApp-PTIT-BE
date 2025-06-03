package com.ptit.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ptit.service.entity.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query(value = "SELECT * FROM exam ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Exam> findRandomExam();
}
package com.ptit.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptit.service.entity.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
}
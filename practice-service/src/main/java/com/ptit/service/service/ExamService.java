package com.ptit.service.service;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExamService {
    Page<Exam> findAll(Pageable pageable);

    ExamDTO updateExam(ExamDTO exam);

    void delete(Long id);

    ExamDTO findById(Long id);

    Exam createExam(String title, String description);

//    ExamDTO getRandomExamAndStart(Long studentId);
}

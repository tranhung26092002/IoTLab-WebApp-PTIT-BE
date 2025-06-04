package com.ptit.service.service;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.entity.Exam;

import java.util.List;

public interface ExamService {
    List<ExamDTO> findAll();

    ExamDTO updateExam(ExamDTO exam);

    void delete(Long id);

    ExamDTO findById(Long id);

    Exam createExam(String title, String description);
    
//    ExamDTO getRandomExamAndStart(Long studentId);
}

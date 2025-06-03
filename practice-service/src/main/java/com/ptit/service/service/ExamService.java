package com.ptit.service.service;

import java.util.List;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.entity.Exam;

public interface ExamService {
    List<Exam> findAll();
    
    Exam updateExam(ExamDTO exam);
    
    void delete(Long id);
    
    Exam findById(Long id);
    
    Exam createExam(String title, String description);

    Exam getRandomExam();

    Exam getRandomExamAndStart(Long studentId);
}

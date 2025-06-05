package com.ptit.service.service;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.entity.Exam;
import com.ptit.service.response.ResponsePage;
import org.springframework.data.domain.Pageable;

public interface ExamService {
    ResponsePage<Exam, ExamDTO> findAll(Pageable pageable);

    ExamDTO updateExam(ExamDTO exam);

    void delete(Long id);

    ExamDTO findById(Long id);

    Exam createExam(String title, String description);

//    ExamDTO getRandomExamAndStart(Long studentId);
}

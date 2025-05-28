package com.ptit.service.service;

import com.ptit.service.dto.StudentAnswerDTO;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAnswerService {
    private final StudentAnswerRepository studentAnswerRepository;

    public List<StudentAnswer> findAll() {
        return studentAnswerRepository.findAll();
    }

    public List<StudentAnswer> findByStudentExamId(Long studentExamId) {
        return studentAnswerRepository.findByStudentExamId(studentExamId);
    }

    public List<StudentAnswer> findByQuestionId(Long questionId) {
        return studentAnswerRepository.findByQuestionId(questionId);
    }

    public StudentAnswer save(StudentAnswer studentAnswer) {
        return studentAnswerRepository.save(studentAnswer);
    }

    public StudentAnswer findById(Long id) {
        return studentAnswerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student Answer not found"));
    }
}
package com.ptit.service.service;

import com.ptit.service.dto.StudentExamDTO;
import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.ExamStatus;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.repository.StudentExamRepository;
import com.ptit.service.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentExamService {
    private final StudentExamRepository studentExamRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    public List<StudentExam> findAll() {
        return studentExamRepository.findAll();
    }

    public List<StudentExam> findByStudentId(String studentId) {
        return studentExamRepository.findByStudentId(studentId);
    }

    public List<StudentExam> findByExamId(Long examId) {
        return studentExamRepository.findByExamId(examId);
    }

    public List<StudentExam> findByStatus(ExamStatus status) {
        return studentExamRepository.findByStatus(status);
    }

    public StudentExam save(StudentExam studentExam) {
        return studentExamRepository.save(studentExam);
    }

    public StudentExam findById(Long id) {
        return studentExamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student Exam not found"));
    }

    public List<StudentAnswer> findAnswersByStudentExamId(Long studentExamId) {
        return studentAnswerRepository.findByStudentExamId(studentExamId);
    }

    public StudentAnswer saveAnswer(StudentAnswer answer) {
        return studentAnswerRepository.save(answer);
    }
}
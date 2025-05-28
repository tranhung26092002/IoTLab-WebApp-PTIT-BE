package com.ptit.service.service;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.entity.Exam;
import com.ptit.service.entity.ExamStatus;
import com.ptit.service.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;

    public List<Exam> findAll() {
        return examRepository.findAll();
    }

    public List<Exam> findByStatus(ExamStatus status) {
        return examRepository.findByStatus(status);
    }

    public Exam save(Exam exam) {
        return examRepository.save(exam);
    }

    public void delete(Long id) {
        examRepository.deleteById(id);
    }

    public Exam findById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
    }
}
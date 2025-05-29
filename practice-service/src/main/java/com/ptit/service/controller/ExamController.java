package com.ptit.service.controller;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.entity.Exam;
import com.ptit.service.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Exam> createExam(@Valid @RequestBody ExamDTO exam) {
        return ResponseEntity.ok(examService.createExam(
                exam.getTitle(),
                exam.getDescription()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @Valid @RequestBody ExamDTO exam) {
        exam.setId(id);
        return ResponseEntity.ok(examService.updateExam(exam));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.ok().build();
    }
}
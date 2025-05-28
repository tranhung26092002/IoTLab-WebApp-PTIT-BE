package com.ptit.service.controller;

import com.ptit.service.dto.StudentAnswerDTO;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.service.StudentAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student-answers")
@RequiredArgsConstructor
public class StudentAnswerController {
    private final StudentAnswerService studentAnswerService;

    @GetMapping
    public ResponseEntity<List<StudentAnswer>> getAllStudentAnswers() {
        return ResponseEntity.ok(studentAnswerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentAnswer> getStudentAnswerById(@PathVariable Long id) {
        return ResponseEntity.ok(studentAnswerService.findById(id));
    }

    @GetMapping("/student-exam/{studentExamId}")
    public ResponseEntity<List<StudentAnswer>> getStudentAnswersByStudentExamId(@PathVariable Long studentExamId) {
        return ResponseEntity.ok(studentAnswerService.findByStudentExamId(studentExamId));
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<StudentAnswer>> getStudentAnswersByQuestionId(@PathVariable Long questionId) {
        return ResponseEntity.ok(studentAnswerService.findByQuestionId(questionId));
    }

    @PostMapping
    public ResponseEntity<StudentAnswer> createStudentAnswer(@RequestBody StudentAnswer studentAnswer) {
        return ResponseEntity.ok(studentAnswerService.save(studentAnswer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentAnswer> updateStudentAnswer(@PathVariable Long id,
            @RequestBody StudentAnswer studentAnswer) {
        studentAnswer.setId(id);
        return ResponseEntity.ok(studentAnswerService.save(studentAnswer));
    }
}
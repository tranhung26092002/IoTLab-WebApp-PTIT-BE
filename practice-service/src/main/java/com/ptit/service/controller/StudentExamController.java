package com.ptit.service.controller;

import com.ptit.service.dto.StudentExamDTO;
import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.entity.ExamStatus;
import com.ptit.service.service.StudentExamService;
import com.ptit.service.service.ExamGradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/student-exams")
@RequiredArgsConstructor
public class StudentExamController {
    private final StudentExamService studentExamService;
    private final ExamGradingService examGradingService;

    @GetMapping
    public ResponseEntity<List<StudentExam>> getAllStudentExams() {
        return ResponseEntity.ok(studentExamService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentExam> getStudentExamById(@PathVariable Long id) {
        return ResponseEntity.ok(studentExamService.findById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentExam>> getStudentExamsByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(studentExamService.findByStudentId(studentId));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<StudentExam>> getStudentExamsByExamId(@PathVariable Long examId) {
        return ResponseEntity.ok(studentExamService.findByExamId(examId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<StudentExam>> getStudentExamsByStatus(@PathVariable ExamStatus status) {
        return ResponseEntity.ok(studentExamService.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<StudentExam> startExam(@Valid @RequestBody StudentExam studentExam) {
        return ResponseEntity.ok(studentExamService.save(studentExam));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitExam(@PathVariable Long id) {
        examGradingService.gradeMultipleChoiceAnswers(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/answers")
    public ResponseEntity<List<StudentAnswer>> getStudentAnswers(@PathVariable Long id) {
        return ResponseEntity.ok(studentExamService.findAnswersByStudentExamId(id));
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity<StudentAnswer> saveAnswer(@PathVariable Long id,
            @Valid @RequestBody StudentAnswer answer) {
        answer.setStudentExam(studentExamService.findById(id));
        return ResponseEntity.ok(studentExamService.saveAnswer(answer));
    }

    @PostMapping("/answers/{answerId}/grade")
    public ResponseEntity<Void> gradeEssayAnswer(@PathVariable Long answerId,
            @RequestParam double score) {
        examGradingService.gradeEssayAnswer(answerId, score);
        return ResponseEntity.ok().build();
    }
}
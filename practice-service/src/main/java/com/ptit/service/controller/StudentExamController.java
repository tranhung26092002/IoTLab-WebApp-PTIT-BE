package com.ptit.service.controller;

import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.entity.enums.ExamStatus;
import com.ptit.service.service.StudentExamService;
import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.dto.StartExamDTO;
import com.ptit.service.dto.StudentAnswerDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student-exams")
@RequiredArgsConstructor
public class StudentExamController {
    private final StudentExamService studentExamService;

    @GetMapping
    public ResponseEntity<List<StudentExam>> getAllStudentExams() {
        return ResponseEntity.ok(studentExamService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentExam> getStudentExamById(@PathVariable Long id) {
        return ResponseEntity.ok(studentExamService.findById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentExam>> getStudentExamsByStudentId(@PathVariable Long studentId) {
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
    public ResponseEntity<StudentExam> startExam(@Valid @RequestBody StartExamDTO startExamDTO) {
        return ResponseEntity.ok(studentExamService.startExam(startExamDTO));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Void> submitExam(@PathVariable Long id) {
        studentExamService.gradeMultipleChoiceAnswers(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/answers")
    public ResponseEntity<List<StudentAnswer>> getStudentAnswers(@PathVariable Long id) {
        return ResponseEntity.ok(studentExamService.findAnswersByStudentExamId(id));
    }

    @PostMapping("/{studentExamId}/answers")
    public ResponseEntity<StudentAnswer> saveAnswer(
            @PathVariable Long studentExamId,
            @RequestPart("answer") StudentAnswerDTO answerDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        StudentAnswer answer = studentExamService.saveAnswer(studentExamId, answerDTO, images);
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/answers/{answerId}/grade")
    public ResponseEntity<Void> gradeEssayAnswer(@PathVariable Long answerId,
            @RequestParam double score) {
        studentExamService.gradeEssayAnswer(answerId, score);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<StudentExamResult> getStudentExamResult(@PathVariable Long id) {
        return ResponseEntity.ok(studentExamService.getStudentExamResult(id));
    }

    @GetMapping("/student/{studentId}/completed")
    public ResponseEntity<List<StudentExam>> getCompletedExamsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentExamService.findCompletedExamsByStudentId(studentId));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<StudentExam> getStudentExamDetails(@PathVariable Long id) {
        return ResponseEntity.ok(studentExamService.findByIdWithAnswers(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StudentExam> updateExamStatus(
            @PathVariable Long id,
            @RequestParam ExamStatus status) {
        return ResponseEntity.ok(studentExamService.updateStatus(id, status));
    }

    @GetMapping("/exam/{examId}/statistics")
    public ResponseEntity<Map<String, Object>> getExamStatistics(@PathVariable Long examId) {
        return ResponseEntity.ok(studentExamService.getExamStatistics(examId));
    }

    @GetMapping("/student/{studentId}/statistics")
    public ResponseEntity<Map<String, Object>> getStudentStatistics(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentExamService.getStudentStatistics(studentId));
    }

    @GetMapping("/exam/{examId}/top-performers")
    public ResponseEntity<List<StudentExamResult>> getTopPerformers(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(studentExamService.getTopPerformers(examId, limit));
    }

    @GetMapping("/exam/{examId}/passing-rate")
    public ResponseEntity<Double> getPassingRate(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "5.0") double passingScore) {
        return ResponseEntity.ok(studentExamService.getPassingRate(examId, passingScore));
    }
}
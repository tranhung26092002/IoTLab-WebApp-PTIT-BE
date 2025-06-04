package com.ptit.service.controller;

import com.ptit.service.dto.StudentProgressDTO;
import com.ptit.service.service.StudentProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class StudentProgressController {

    private final StudentProgressService studentProgressService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentProgressDTO>> getStudentProgress(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(studentProgressService.getStudentProgress(studentId));
    }

    @GetMapping("/student/{studentId}/practice/{practiceId}")
    public ResponseEntity<StudentProgressDTO> getPracticeProgress(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        return ResponseEntity.ok(studentProgressService.getPracticeProgress(studentId, practiceId));
    }

    @PostMapping("/student/{studentId}/practice/{practiceId}/start")
    public ResponseEntity<StudentProgressDTO> startPractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        return ResponseEntity.ok(studentProgressService.startPractice(studentId, practiceId));
    }

    @PostMapping("/student/{studentId}/practice/{practiceId}/complete")
    public ResponseEntity<StudentProgressDTO> completePractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId,
            @RequestParam(required = false) Double score,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(studentProgressService.completePractice(studentId, practiceId, score, comment));
    }

    @PutMapping("/student/{studentId}/practice/{practiceId}/score")
    public ResponseEntity<StudentProgressDTO> updatePracticeScore(
            @PathVariable Long studentId,
            @PathVariable Long practiceId,
            @RequestParam Double score,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(studentProgressService.updatePracticeScore(studentId, practiceId, score, comment));
    }

    @GetMapping("/student/{studentId}/practice/{practiceId}/can-start")
    public ResponseEntity<Boolean> canStartPractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        return ResponseEntity.ok(studentProgressService.canStartPractice(studentId, practiceId));
    }

    @GetMapping("/student/{studentId}/completed-all")
    public ResponseEntity<Boolean> hasCompletedAllPractices(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(studentProgressService.hasCompletedAllPractices(studentId));
    }

    @GetMapping("/student/{studentId}/completion-rate")
    public ResponseEntity<Double> getCompletionRate(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(studentProgressService.calculateCompletionRate(studentId));
    }
}
package com.ptit.service.controller;

import com.ptit.service.dto.StudentProgressDTO;
import com.ptit.service.entity.Student;
import com.ptit.service.entity.StudentProgress;
import com.ptit.service.service.StudentProgressService;
import com.ptit.service.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class StudentProgressController {

    private final StudentProgressService studentProgressService;
    private final StudentService studentService;

    private Long getStudentIdFromUserId(Long userId) {
        return studentService.findByUserId(userId)
                .map(Student::getId)
                .orElseThrow(() -> new RuntimeException("Student not found for userId: " + userId));
    }

    @GetMapping("/student/{userId}")
    public ResponseEntity<List<StudentProgressDTO>> getStudentProgress(
            @PathVariable Long userId) {
        Long studentId = getStudentIdFromUserId(userId);
        List<StudentProgress> progressList = studentProgressService.getStudentProgress(studentId);
        List<StudentProgressDTO> progressDTOList = progressList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(progressDTOList);
    }

    @GetMapping("/student/{userId}/practice/{practiceId}")
    public ResponseEntity<StudentProgressDTO> getPracticeProgress(
            @PathVariable Long userId,
            @PathVariable Long practiceId) {
        Long studentId = getStudentIdFromUserId(userId);
        StudentProgress progress = studentProgressService.getPracticeProgress(studentId, practiceId);
        return ResponseEntity.ok(convertToDTO(progress));
    }

    @PostMapping("/student/{userId}/practice/{practiceId}/start")
    public ResponseEntity<StudentProgressDTO> startPractice(
            @PathVariable Long userId,
            @PathVariable Long practiceId) {
        Long studentId = getStudentIdFromUserId(userId);
        StudentProgress progress = studentProgressService.startPractice(studentId, practiceId);
        return ResponseEntity.ok(convertToDTO(progress));
    }

    @PostMapping("/student/{userId}/practice/{practiceId}/complete")
    public ResponseEntity<StudentProgressDTO> completePractice(
            @PathVariable Long userId,
            @PathVariable Long practiceId,
            @RequestParam(required = false) Double score,
            @RequestParam(required = false) String comment) {
        Long studentId = getStudentIdFromUserId(userId);
        StudentProgress progress = studentProgressService.completePractice(studentId, practiceId, score, comment);
        return ResponseEntity.ok(convertToDTO(progress));
    }

    @PutMapping("/student/{userId}/practice/{practiceId}/score")
    public ResponseEntity<StudentProgressDTO> updatePracticeScore(
            @PathVariable Long userId,
            @PathVariable Long practiceId,
            @RequestParam Double score,
            @RequestParam(required = false) String comment) {
        Long studentId = getStudentIdFromUserId(userId);
        StudentProgress progress = studentProgressService.updatePracticeScore(studentId, practiceId, score, comment);
        return ResponseEntity.ok(convertToDTO(progress));
    }

    @GetMapping("/student/{userId}/practice/{practiceId}/can-start")
    public ResponseEntity<Boolean> canStartPractice(
            @PathVariable Long userId,
            @PathVariable Long practiceId) {
        Long studentId = getStudentIdFromUserId(userId);
        boolean canStart = studentProgressService.canStartPractice(studentId, practiceId);
        return ResponseEntity.ok(canStart);
    }

    @GetMapping("/student/{userId}/completed-all")
    public ResponseEntity<Boolean> hasCompletedAllPractices(
            @PathVariable Long userId) {
        Long studentId = getStudentIdFromUserId(userId);
        boolean completed = studentProgressService.hasCompletedAllPractices(studentId);
        return ResponseEntity.ok(completed);
    }

    @GetMapping("/student/{userId}/completion-rate")
    public ResponseEntity<Double> getCompletionRate(
            @PathVariable Long userId) {
        Long studentId = getStudentIdFromUserId(userId);
        double rate = studentProgressService.calculateCompletionRate(studentId);
        return ResponseEntity.ok(rate);
    }

    private StudentProgressDTO convertToDTO(StudentProgress progress) {
        StudentProgressDTO dto = new StudentProgressDTO();
        dto.setId(progress.getId());
        dto.setStudentId(progress.getStudent().getId());
        dto.setPracticeId(progress.getPractice().getId());
        dto.setStatus(progress.getStatus());
        dto.setScore(progress.getScore());
        dto.setComment(progress.getComment());
        dto.setCompletedAt(progress.getCompletedAt());
        dto.setCreatedAt(progress.getCreatedAt());
        dto.setUpdatedAt(progress.getUpdatedAt());
        return dto;
    }
}
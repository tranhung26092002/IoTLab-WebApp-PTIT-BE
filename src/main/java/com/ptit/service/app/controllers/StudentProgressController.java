package com.ptit.service.app.controllers;

import com.ptit.service.app.dtos.StudentProgressDTO;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.StudentProgress;
import com.ptit.service.domain.service.StudentProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class StudentProgressController {

    private final StudentProgressService studentProgressService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentProgressDTO>> getStudentProgress(
            @PathVariable Long studentId) {
        List<StudentProgress> progressList = studentProgressService.getStudentProgress(studentId);
        List<StudentProgressDTO> progressDTOList = progressList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(progressDTOList);
    }

    @GetMapping("/student/{studentId}/practice/{practiceId}")
    public ResponseEntity<StudentProgressDTO> getPracticeProgress(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        StudentProgress progress = studentProgressService.getPracticeProgress(studentId, practiceId);
        return ResponseEntity.ok(convertToDTO(progress));
    }

    @PostMapping("/student/{studentId}/practice/{practiceId}/start")
    public ResponseEntity<StudentProgressDTO> startPractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        StudentProgress progress = studentProgressService.startPractice(studentId, practiceId);
        return ResponseEntity.ok(convertToDTO(progress));
    }

    @PostMapping("/student/{studentId}/practice/{practiceId}/complete")
    public ResponseEntity<StudentProgressDTO> completePractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId,
            @RequestParam(required = false) Double score,
            @RequestParam(required = false) String comment) {
        StudentProgress progress = studentProgressService.completePractice(studentId, practiceId, score, comment);
        return ResponseEntity.ok(convertToDTO(progress));
    }

    @PutMapping("/student/{studentId}/practice/{practiceId}/score")
    public ResponseEntity<StudentProgressDTO> updatePracticeScore(
            @PathVariable Long studentId,
            @PathVariable Long practiceId,
            @RequestParam Double score,
            @RequestParam(required = false) String comment) {
        StudentProgress progress = studentProgressService.updatePracticeScore(studentId, practiceId, score, comment);
        return ResponseEntity.ok(convertToDTO(progress));
    }

    @GetMapping("/student/{studentId}/practice/{practiceId}/can-start")
    public ResponseEntity<Boolean> canStartPractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        boolean canStart = studentProgressService.canStartPractice(studentId, practiceId);
        return ResponseEntity.ok(canStart);
    }

    @GetMapping("/student/{studentId}/completed-all")
    public ResponseEntity<Boolean> hasCompletedAllPractices(
            @PathVariable Long studentId) {
        boolean completed = studentProgressService.hasCompletedAllPractices(studentId);
        return ResponseEntity.ok(completed);
    }

    @GetMapping("/student/{studentId}/completion-rate")
    public ResponseEntity<Double> getCompletionRate(
            @PathVariable Long studentId) {
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
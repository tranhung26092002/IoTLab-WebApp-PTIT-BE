package com.ptit.service.controller;

import com.ptit.service.dto.StudentProgressDTO;
import com.ptit.service.response.DataResponse;
import com.ptit.service.service.StudentProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class StudentProgressController extends BaseController {

    private final StudentProgressService studentProgressService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<DataResponse<List<StudentProgressDTO>>> getStudentProgress(
            @PathVariable Long studentId) {
        List<StudentProgressDTO> progress = studentProgressService.getStudentProgress(studentId);
        return success(progress);
    }

    @GetMapping("/student/{studentId}/practice/{practiceId}")
    public ResponseEntity<DataResponse<StudentProgressDTO>> getPracticeProgress(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        StudentProgressDTO progress = studentProgressService.getPracticeProgress(studentId, practiceId);
        return success(progress);
    }

    @PostMapping("/student/{studentId}/practice/{practiceId}/start")
    public ResponseEntity<DataResponse<StudentProgressDTO>> startPractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        StudentProgressDTO progress = studentProgressService.startPractice(studentId, practiceId);
        return created(progress);
    }

    @PostMapping("/student/{studentId}/practice/{practiceId}/complete")
    public ResponseEntity<DataResponse<StudentProgressDTO>> completePractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId,
            @RequestParam(required = false) Double score,
            @RequestParam(required = false) String comment) {
        StudentProgressDTO progress = studentProgressService.completePractice(studentId, practiceId, score, comment);
        return success(progress);
    }

    @PutMapping("/student/{studentId}/practice/{practiceId}/score")
    public ResponseEntity<DataResponse<StudentProgressDTO>> updatePracticeScore(
            @PathVariable Long studentId,
            @PathVariable Long practiceId,
            @RequestParam Double score,
            @RequestParam(required = false) String comment) {
        StudentProgressDTO progress = studentProgressService.updatePracticeScore(studentId, practiceId, score, comment);
        return success(progress);
    }

    @GetMapping("/student/{studentId}/practice/{practiceId}/can-start")
    public ResponseEntity<DataResponse<Boolean>> canStartPractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        Boolean canStart = studentProgressService.canStartPractice(studentId, practiceId);
        return success(canStart);
    }

    @GetMapping("/student/{studentId}/completed-all")
    public ResponseEntity<DataResponse<Boolean>> hasCompletedAllPractices(
            @PathVariable Long studentId) {
        Boolean completed = studentProgressService.hasCompletedAllPractices(studentId);
        return success(completed);
    }

    @GetMapping("/student/{studentId}/completion-rate")
    public ResponseEntity<DataResponse<Double>> getCompletionRate(
            @PathVariable Long studentId) {
        Double rate = studentProgressService.calculateCompletionRate(studentId);
        return success(rate);
    }
}
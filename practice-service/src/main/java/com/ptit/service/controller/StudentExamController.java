package com.ptit.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.dto.StudentAnswerListDTO;
import com.ptit.service.dto.StudentExamDTO;
import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.entity.StudentExam;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/student-exams")
@RequiredArgsConstructor
public class StudentExamController {
    private final StudentExamService studentExamService;

    @GetMapping
    public ResponseEntity<ResponsePage<StudentExam, StudentExamDTO>> getAllStudentExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(studentExamService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentExamDTO> getStudentExamById(@PathVariable Long id) {
        return ResponseEntity.ok(studentExamService.findById(id));
    }

    @GetMapping("/student/{studentId}/current")
    public ResponseEntity<StudentExamDTO> getCurrentExam(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentExamService.findCurrentExamByStudentId(studentId));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<StudentExamResult> submitExam(
            @PathVariable Long id,
            @RequestParam(value = "answers", required = false) String answersJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {

        // Parse and save answers if provided, which will also calculate scores
        if (answersJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            StudentAnswerListDTO answersDTO = objectMapper.readValue(answersJson, StudentAnswerListDTO.class);
            studentExamService.saveAnswers(id, answersDTO, images);
        }

        // Return the exam result which will include the calculated scores
        return ResponseEntity.ok(studentExamService.getStudentExamResult(id));
    }

    @PostMapping("/answers/{answerId}/grade")
    public ResponseEntity<StudentExamResult> gradeEssayAnswer(
            @PathVariable Long answerId,
            @RequestParam double score) {
        // Chấm điểm tự luận và trả về kết quả mới
        studentExamService.gradeEssayAnswer(answerId, score);
        return ResponseEntity.ok(studentExamService.getStudentExamResult(answerId));
    }

    @GetMapping("/{studentExamId}/result")
    public ResponseEntity<StudentExamResult> getStudentExamResult(@PathVariable Long studentExamId) {
        return ResponseEntity.ok(studentExamService.getStudentExamResult(studentExamId));
    }

    @GetMapping("/{studentExamId}/details")
    public ResponseEntity<StudentExamDTO> getStudentExamDetails(@PathVariable Long studentExamId) {
        return ResponseEntity.ok(studentExamService.findByIdWithAnswers(studentExamId));
    }
}
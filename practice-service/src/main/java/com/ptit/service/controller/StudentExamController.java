package com.ptit.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.dto.StudentAnswerListDTO;
import com.ptit.service.dto.StudentExamDTO;
import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.entity.StudentExam;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class StudentExamController extends BaseController {
    private final StudentExamService studentExamService;

    @GetMapping
    public ResponseEntity<DataResponse<PaginationData<StudentExamDTO>>> getAllStudentExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<StudentExam> examPage = studentExamService.findAll(pageable);
        PaginationData<StudentExamDTO> paginationData = PaginationData.fromPageWithMapping(examPage, StudentExamDTO.class);
        return successWithPagination(paginationData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<StudentExamDTO>> getStudentExamById(@PathVariable Long id) {
        StudentExamDTO exam = studentExamService.findById(id);
        return success(exam);
    }

    @GetMapping("/student/{studentId}/current")
    public ResponseEntity<DataResponse<StudentExamDTO>> getCurrentExam(@PathVariable Long studentId) {
        StudentExamDTO exam = studentExamService.findCurrentExamByStudentId(studentId);
        return success(exam);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<DataResponse<StudentExamResult>> submitExam(
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
        StudentExamResult result = studentExamService.getStudentExamResult(id);
        return success(result);
    }

    @PostMapping("/{studentExamId}/questions/{questionId}/grade")
    public ResponseEntity<DataResponse<StudentExamResult>> gradeEssayAnswer(
            @PathVariable Long studentExamId,
            @PathVariable Long questionId,
            @RequestParam double score) {
        // Chấm điểm tự luận và trả về kết quả mới
        studentExamService.gradeEssayAnswer(studentExamId, questionId, score);
        StudentExamResult result = studentExamService.getStudentExamResult(studentExamId);
        return success(result);
    }

    @GetMapping("/{studentExamId}/result")
    public ResponseEntity<DataResponse<StudentExamResult>> getStudentExamResult(@PathVariable Long studentExamId) {
        StudentExamResult result = studentExamService.getStudentExamResult(studentExamId);
        return success(result);
    }

    @GetMapping("/{studentExamId}/details")
    public ResponseEntity<DataResponse<StudentExamDTO>> getStudentExamDetails(@PathVariable Long studentExamId) {
        StudentExamDTO exam = studentExamService.findByIdWithAnswers(studentExamId);
        return success(exam);
    }
}
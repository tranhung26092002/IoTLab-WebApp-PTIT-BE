package com.ptit.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.dto.StudentAnswerListDTO;
import com.ptit.service.dto.StudentExamDTO;
import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.entity.StudentExam;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.service.StudentExamService;
import io.swagger.annotations.*;
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
@Api(tags = "Student Exam", description = "APIs quản lý bài thi của sinh viên, nộp bài và chấm điểm")
public class StudentExamController extends BaseController {
    private final StudentExamService studentExamService;

    @GetMapping
    @ApiOperation(value = "Lấy danh sách bài thi của sinh viên", notes = "Trả về danh sách bài thi có phân trang")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền truy cập")
    })
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
    @ApiOperation(value = "Lấy thông tin bài thi sinh viên theo ID", notes = "Trả về chi tiết bài thi của sinh viên")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thi")
    })
    public ResponseEntity<DataResponse<StudentExamDTO>> getStudentExamById(@PathVariable Long id) {
        StudentExamDTO exam = studentExamService.findById(id);
        return success(exam);
    }

    @GetMapping("/student/{studentId}/current")
    @ApiOperation(value = "Lấy bài thi hiện tại của sinh viên", notes = "Trả về bài thi đang diễn ra của sinh viên")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không có bài thi hiện tại")
    })
    public ResponseEntity<DataResponse<StudentExamDTO>> getCurrentExam(@PathVariable Long studentId) {
        StudentExamDTO exam = studentExamService.findCurrentExamByStudentId(studentId);
        return success(exam);
    }

    @PostMapping("/{id}/submit")
    @ApiOperation(value = "Nộp bài thi", notes = "Nộp bài thi với câu trả lời và hình ảnh")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Nộp bài thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thi")
    })
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
    @ApiOperation(value = "Chấm điểm câu hỏi tự luận", notes = "Chấm điểm câu hỏi tự luận và cập nhật kết quả")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Chấm điểm thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thi hoặc câu hỏi")
    })
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
    @ApiOperation(value = "Lấy kết quả bài thi", notes = "Trả về kết quả chi tiết của bài thi")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thi")
    })
    public ResponseEntity<DataResponse<StudentExamResult>> getStudentExamResult(@PathVariable Long studentExamId) {
        StudentExamResult result = studentExamService.getStudentExamResult(studentExamId);
        return success(result);
    }

    @GetMapping("/{studentExamId}/details")
    @ApiOperation(value = "Lấy chi tiết bài thi với câu trả lời", notes = "Trả về chi tiết bài thi bao gồm câu trả lời của sinh viên")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thi")
    })
    public ResponseEntity<DataResponse<StudentExamDTO>> getStudentExamDetails(@PathVariable Long studentExamId) {
        StudentExamDTO exam = studentExamService.findByIdWithAnswers(studentExamId);
        return success(exam);
    }
}
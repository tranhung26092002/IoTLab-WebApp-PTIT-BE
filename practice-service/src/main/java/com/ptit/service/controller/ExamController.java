package com.ptit.service.controller;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.entity.Exam;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.service.ExamService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Api(tags = "Exam Management", description = "APIs quản lý bài thi và cấu hình thi")
public class ExamController extends BaseController {
    private final ExamService examService;

    @GetMapping
    @ApiOperation(value = "Lấy danh sách tất cả bài thi", notes = "Trả về danh sách bài thi có phân trang")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền truy cập")
    })
    public ResponseEntity<DataResponse<PaginationData<ExamDTO>>> getAllExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Exam> examPage = examService.findAll(pageable);
        PaginationData<ExamDTO> paginationData = PaginationData.fromPageWithMapping(examPage, ExamDTO.class);
        return successWithPagination(paginationData);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Lấy thông tin bài thi theo ID", notes = "Trả về chi tiết bài thi")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thi")
    })
    public ResponseEntity<DataResponse<ExamDTO>> getExamById(@PathVariable Long id) {
        ExamDTO exam = examService.findById(id);
        return success(exam);
    }

//    @GetMapping("/random/{studentId}")
//    public ResponseEntity<DataResponse<ExamDTO>> getRandomExamAndStart(@PathVariable Long studentId) {
//        ExamDTO exam = examService.getRandomExamAndStart(studentId);
//        return success(exam);
//    }

    @PostMapping
    @ApiOperation(value = "Tạo bài thi mới", notes = "Tạo bài thi với thông tin cơ bản")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Tạo thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền tạo")
    })
    public ResponseEntity<DataResponse<ExamDTO>> createExam(@Valid @RequestBody ExamDTO exam) {
        Exam createdExam = examService.createExam(
                exam.getTitle(),
                exam.getDescription());
        ExamDTO response = convertToExamDTO(createdExam);
        return created(response);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Cập nhật bài thi", notes = "Cập nhật thông tin bài thi theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thi"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<DataResponse<ExamDTO>> updateExam(@PathVariable Long id, @Valid @RequestBody ExamDTO exam) {
        exam.setId(id);
        ExamDTO updatedExam = examService.updateExam(exam);
        return success(updatedExam);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Xóa bài thi", notes = "Xóa bài thi theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Xóa thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thi")
    })
    public ResponseEntity<DataResponse<Object>> deleteExam(@PathVariable Long id) {
        examService.delete(id);
        return noContent();
    }

    // Helper method để convert Exam entity sang ExamDTO
    private ExamDTO convertToExamDTO(Exam exam) {
        ExamDTO dto = new ExamDTO();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setCreatedAt(exam.getCreatedAt());
        dto.setUpdatedAt(exam.getUpdatedAt());
        return dto;
    }
}
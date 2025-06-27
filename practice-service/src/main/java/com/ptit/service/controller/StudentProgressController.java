package com.ptit.service.controller;

import com.ptit.service.dto.StudentProgressDTO;
import com.ptit.service.response.DataResponse;
import com.ptit.service.service.StudentProgressService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
@Api(tags = "Student Progress", description = "APIs theo dõi tiến độ học tập của sinh viên")
public class StudentProgressController extends BaseController {

    private final StudentProgressService studentProgressService;

    @GetMapping("/student/{studentId}")
    @ApiOperation(value = "Lấy tiến độ học tập của sinh viên", notes = "Trả về danh sách tiến độ học tập của sinh viên")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy sinh viên")
    })
    public ResponseEntity<DataResponse<List<StudentProgressDTO>>> getStudentProgress(
            @PathVariable Long studentId) {
        List<StudentProgressDTO> progress = studentProgressService.getStudentProgress(studentId);
        return success(progress);
    }

    @GetMapping("/student/{studentId}/practice/{practiceId}")
    @ApiOperation(value = "Lấy tiến độ bài thực hành cụ thể", notes = "Trả về tiến độ của sinh viên cho bài thực hành")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy tiến độ")
    })
    public ResponseEntity<DataResponse<StudentProgressDTO>> getPracticeProgress(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        StudentProgressDTO progress = studentProgressService.getPracticeProgress(studentId, practiceId);
        return success(progress);
    }

    @PostMapping("/student/{studentId}/practice/{practiceId}/start")
    @ApiOperation(value = "Bắt đầu bài thực hành", notes = "Đánh dấu sinh viên bắt đầu làm bài thực hành")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Bắt đầu thành công"),
        @ApiResponse(code = 400, message = "Không thể bắt đầu bài thực hành")
    })
    public ResponseEntity<DataResponse<StudentProgressDTO>> startPractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        StudentProgressDTO progress = studentProgressService.startPractice(studentId, practiceId);
        return created(progress);
    }

    @PostMapping("/student/{studentId}/practice/{practiceId}/complete")
    @ApiOperation(value = "Hoàn thành bài thực hành", notes = "Đánh dấu sinh viên hoàn thành bài thực hành với điểm số")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Hoàn thành thành công"),
        @ApiResponse(code = 400, message = "Không thể hoàn thành bài thực hành")
    })
    public ResponseEntity<DataResponse<StudentProgressDTO>> completePractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId,
            @RequestParam(required = false) Double score,
            @RequestParam(required = false) String comment) {
        StudentProgressDTO progress = studentProgressService.completePractice(studentId, practiceId, score, comment);
        return success(progress);
    }

    @PutMapping("/student/{studentId}/practice/{practiceId}/score")
    @ApiOperation(value = "Cập nhật điểm bài thực hành", notes = "Cập nhật điểm số cho bài thực hành đã hoàn thành")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy tiến độ")
    })
    public ResponseEntity<DataResponse<StudentProgressDTO>> updatePracticeScore(
            @PathVariable Long studentId,
            @PathVariable Long practiceId,
            @RequestParam Double score,
            @RequestParam(required = false) String comment) {
        StudentProgressDTO progress = studentProgressService.updatePracticeScore(studentId, practiceId, score, comment);
        return success(progress);
    }

    @GetMapping("/student/{studentId}/practice/{practiceId}/can-start")
    @ApiOperation(value = "Kiểm tra có thể bắt đầu bài thực hành", notes = "Kiểm tra xem sinh viên có thể bắt đầu bài thực hành không")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công")
    })
    public ResponseEntity<DataResponse<Boolean>> canStartPractice(
            @PathVariable Long studentId,
            @PathVariable Long practiceId) {
        Boolean canStart = studentProgressService.canStartPractice(studentId, practiceId);
        return success(canStart);
    }

    @GetMapping("/student/{studentId}/completed-all")
    @ApiOperation(value = "Kiểm tra hoàn thành tất cả bài thực hành", notes = "Kiểm tra xem sinh viên đã hoàn thành tất cả bài thực hành chưa")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công")
    })
    public ResponseEntity<DataResponse<Boolean>> hasCompletedAllPractices(
            @PathVariable Long studentId) {
        Boolean completed = studentProgressService.hasCompletedAllPractices(studentId);
        return success(completed);
    }

    @GetMapping("/student/{studentId}/completion-rate")
    @ApiOperation(value = "Lấy tỷ lệ hoàn thành", notes = "Trả về tỷ lệ hoàn thành bài thực hành của sinh viên")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công")
    })
    public ResponseEntity<DataResponse<Double>> getCompletionRate(
            @PathVariable Long studentId) {
        Double rate = studentProgressService.calculateCompletionRate(studentId);
        return success(rate);
    }
}
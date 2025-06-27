package com.ptit.service.controller;

import com.ptit.service.dto.ReportDTO;
import com.ptit.service.dto.ReportFilterDTO;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.response.ReportResponse;
import com.ptit.service.entity.Report;
import com.ptit.service.entity.enums.ReportStatus;
import com.ptit.service.service.ReportService;
import com.ptit.service.util.Constant;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@Api(tags = "Report Management", description = "APIs quản lý báo cáo thực hành và đánh giá")
public class ReportController extends BaseController {
    private final ReportService reportService;

    @GetMapping()
    @ApiOperation(value = "Lấy danh sách tất cả báo cáo", notes = "Trả về danh sách báo cáo có phân trang")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền truy cập")
    })
    public ResponseEntity<DataResponse<PaginationData<ReportResponse>>> getReports(Pageable pageable) {
        Page<Report> page = reportService.getReports(pageable);
        PaginationData<ReportResponse> paginationData = PaginationData.fromPageWithMapping(page, ReportResponse.class);
        return successWithPagination(paginationData);
    }

    private void processFilter(ReportFilterDTO reportFilterDTO) {
        // Danh sách các trường hợp cho phép sắp xếp
        List<String> allowedFields = Arrays.asList(
                "id", "title", "classGroup", "className", "shift", "status");

        if (!allowedFields.contains(reportFilterDTO.getSortField())) {
            reportFilterDTO.setSortField("id");
        }

        // Chuyển đổi ngày tháng
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        reportFilterDTO.setStartDate(parseDate(reportFilterDTO.getStartDate(), formatter));
        reportFilterDTO.setEndDate(parseDate(reportFilterDTO.getEndDate(), formatter));
    }

    @GetMapping("/filter")
    @ApiOperation(value = "Lọc báo cáo theo tiêu chí", notes = "Lọc báo cáo theo các tiêu chí khác nhau")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<DataResponse<PaginationData<ReportResponse>>> getReportsByFilter(
            @ModelAttribute ReportFilterDTO reportFilterDTO,
            Pageable pageable) {
        processFilter(reportFilterDTO);
        Page<Report> page = reportService.getReportsFilter(reportFilterDTO, pageable);
        PaginationData<ReportResponse> paginationData = PaginationData.fromPageWithMapping(page, ReportResponse.class);
        return successWithPagination(paginationData);
    }

    @GetMapping("/me")
    @ApiOperation(value = "Lấy báo cáo của tôi", notes = "Lấy danh sách báo cáo của sinh viên hiện tại")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực")
    })
    public ResponseEntity<DataResponse<PaginationData<ReportResponse>>> getReportsOfMe(
            @RequestHeader(name = Constant.headerUserId) Long studentId,
            @ModelAttribute ReportFilterDTO reportFilterDTO,
            Pageable pageable) {
        processFilter(reportFilterDTO);
        reportFilterDTO.setStudentId(studentId); // Lọc theo studentId
        Page<Report> page = reportService.getReportsFilter(reportFilterDTO, pageable);
        PaginationData<ReportResponse> paginationData = PaginationData.fromPageWithMapping(page, ReportResponse.class);
        return successWithPagination(paginationData);
    }

    // get all reports by student id
    @GetMapping("/student/{studentId}")
    @ApiOperation(value = "Lấy báo cáo theo ID sinh viên", notes = "Lấy danh sách báo cáo của sinh viên cụ thể")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy sinh viên")
    })
    public ResponseEntity<DataResponse<PaginationData<ReportResponse>>> getReportsByStudentId(@PathVariable Long studentId, Pageable pageable) {
        Page<Report> page = reportService.getReportsByStudentId(studentId, pageable);
        PaginationData<ReportResponse> paginationData = PaginationData.fromPageWithMapping(page, ReportResponse.class);
        return successWithPagination(paginationData);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Lấy thông tin báo cáo theo ID", notes = "Trả về chi tiết báo cáo")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy báo cáo")
    })
    public ResponseEntity<DataResponse<ReportResponse>> getReport(@PathVariable Long id) {
        ReportResponse report = reportService.getReport(id);
        return success(report);
    }

    @PostMapping
    @ApiOperation(value = "Nộp báo cáo", notes = "Tạo báo cáo mới với trạng thái SUBMITTED")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Nộp thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa xác thực")
    })
    public ResponseEntity<DataResponse<ReportResponse>> submitReport(@RequestBody ReportDTO reportDTO) {
        reportDTO.setStatus(ReportStatus.SUBMITTED);
        ReportResponse report = reportService.createReport(reportDTO);
        return created(report);
    }

    @PostMapping("/draft")
    @ApiOperation(value = "Lưu báo cáo nháp", notes = "Tạo báo cáo với trạng thái DRAFT")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Lưu nháp thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa xác thực")
    })
    public ResponseEntity<DataResponse<ReportResponse>> saveAsDraft(@RequestBody ReportDTO reportDTO) {
        reportDTO.setStatus(ReportStatus.DRAFT);
        ReportResponse report = reportService.createReport(reportDTO);
        return created(report);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Cập nhật báo cáo", notes = "Cập nhật thông tin báo cáo theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy báo cáo"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<DataResponse<ReportResponse>> updateReport(
            @PathVariable Long id,
            @RequestBody ReportDTO reportDTO) {
        ReportResponse report = reportService.updateReport(id, reportDTO);
        return success(report);
    }

    @PatchMapping("/{id}/status")
    @ApiOperation(value = "Cập nhật trạng thái báo cáo", notes = "Cập nhật trạng thái của báo cáo")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy báo cáo")
    })
    public ResponseEntity<DataResponse<ReportResponse>> updateReportStatus(
            @PathVariable Long id,
            @RequestParam(required = true) ReportStatus status) {
        ReportResponse report = reportService.updateReportStatus(id, status);
        return success(report);
    }

    // update evaluation
    @PatchMapping("/{contentId}/evaluation")
    @ApiOperation(value = "Cập nhật đánh giá", notes = "Cập nhật điểm đánh giá cho báo cáo")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy báo cáo")
    })
    public ResponseEntity<DataResponse<ReportResponse>> updateEvaluation(
            @PathVariable Long contentId,
            @RequestParam(required = true) Double evaluation) {
        ReportResponse report = reportService.updateEvaluation(contentId, evaluation);
        return success(report);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Xóa báo cáo", notes = "Xóa báo cáo theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Xóa thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy báo cáo")
    })
    public ResponseEntity<DataResponse<MessageResponse>> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        MessageResponse response = new MessageResponse();
        response.setMessage("Report deleted successfully");
        return success(response);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "Upload hình ảnh", notes = "Upload hình ảnh cho báo cáo")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Upload thành công"),
        @ApiResponse(code = 400, message = "File không hợp lệ")
    })
    public ResponseEntity<DataResponse<MessageResponse>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = reportService.uploadImage(file);
        MessageResponse response = new MessageResponse();
        response.setMessage(imageUrl);
        return success(response);
    }

    // Hàm chuyển đổi String -> LocalDateTime (trả về null nếu sai format)
    private String parseDate(String dateStr, DateTimeFormatter formatter) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr, formatter).toString(); // Trả về dạng chuẩn ISO 8601
        } catch (Exception e) {
            return null; // Bỏ qua nếu sai format
        }
    }
}

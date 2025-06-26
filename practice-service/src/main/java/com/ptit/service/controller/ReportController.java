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
public class ReportController extends BaseController {
    private final ReportService reportService;

    @GetMapping()
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
    public ResponseEntity<DataResponse<PaginationData<ReportResponse>>> getReportsByFilter(
            @ModelAttribute ReportFilterDTO reportFilterDTO,
            Pageable pageable) {
        processFilter(reportFilterDTO);
        Page<Report> page = reportService.getReportsFilter(reportFilterDTO, pageable);
        PaginationData<ReportResponse> paginationData = PaginationData.fromPageWithMapping(page, ReportResponse.class);
        return successWithPagination(paginationData);
    }

    @GetMapping("/me")
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
    public ResponseEntity<DataResponse<PaginationData<ReportResponse>>> getReportsByStudentId(@PathVariable Long studentId, Pageable pageable) {
        Page<Report> page = reportService.getReportsByStudentId(studentId, pageable);
        PaginationData<ReportResponse> paginationData = PaginationData.fromPageWithMapping(page, ReportResponse.class);
        return successWithPagination(paginationData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<ReportResponse>> getReport(@PathVariable Long id) {
        ReportResponse report = reportService.getReport(id);
        return success(report);
    }

    @PostMapping
    public ResponseEntity<DataResponse<ReportResponse>> submitReport(@RequestBody ReportDTO reportDTO) {
        reportDTO.setStatus(ReportStatus.SUBMITTED);
        ReportResponse report = reportService.createReport(reportDTO);
        return created(report);
    }

    @PostMapping("/draft")
    public ResponseEntity<DataResponse<ReportResponse>> saveAsDraft(@RequestBody ReportDTO reportDTO) {
        reportDTO.setStatus(ReportStatus.DRAFT);
        ReportResponse report = reportService.createReport(reportDTO);
        return created(report);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<ReportResponse>> updateReport(
            @PathVariable Long id,
            @RequestBody ReportDTO reportDTO) {
        ReportResponse report = reportService.updateReport(id, reportDTO);
        return success(report);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DataResponse<ReportResponse>> updateReportStatus(
            @PathVariable Long id,
            @RequestParam(required = true) ReportStatus status) {
        ReportResponse report = reportService.updateReportStatus(id, status);
        return success(report);
    }

    // update evaluation
    @PatchMapping("/{contentId}/evaluation")
    public ResponseEntity<DataResponse<ReportResponse>> updateEvaluation(
            @PathVariable Long contentId,
            @RequestParam(required = true) Double evaluation) {
        ReportResponse report = reportService.updateEvaluation(contentId, evaluation);
        return success(report);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResponse<MessageResponse>> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        MessageResponse response = new MessageResponse();
        response.setMessage("Report deleted successfully");
        return success(response);
    }

    @PostMapping("/upload")
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

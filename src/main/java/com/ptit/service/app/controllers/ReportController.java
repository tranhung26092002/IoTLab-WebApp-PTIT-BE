package com.ptit.service.app.controllers;

import com.ptit.service.app.dtos.ReportDTO;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.ReportResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Report;
import com.ptit.service.domain.enums.ReportStatus;
import com.ptit.service.domain.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    @GetMapping()
    public ResponsePage<Report, ReportResponse> getReports(Pageable pageable) {
        return reportService.getReports(pageable);
    }

    // get all reports by student id
    @GetMapping("/student/{studentId}")
    public ResponsePage<Report, ReportResponse> getReportsByStudentId(@PathVariable Long studentId, Pageable pageable) {
        return reportService.getReportsByStudentId(studentId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    @PostMapping
    public ResponseEntity<ReportResponse> submitReport(@RequestBody ReportDTO reportDTO) {
        reportDTO.setStatus(ReportStatus.SUBMITTED);
        ReportResponse report = reportService.createReport(reportDTO);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/draft")
    public ResponseEntity<ReportResponse> saveAsDraft(@RequestBody ReportDTO reportDTO) {
        reportDTO.setStatus(ReportStatus.DRAFT);
        ReportResponse report = reportService.createReport(reportDTO);
        return ResponseEntity.ok(report);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportResponse> updateReport(
            @PathVariable Long id,
            @RequestBody ReportDTO reportDTO) {
        return ResponseEntity.ok(reportService.updateReport(id, reportDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable Long id,
            @RequestParam(required = true) ReportStatus status) {
        return ResponseEntity.ok(reportService.updateReportStatus(id, status));
    }

    // update evaluation
    @PatchMapping("/{contentId}/evaluation")
    public ResponseEntity<ReportResponse> updateEvaluation(
            @PathVariable Long contentId,
            @RequestParam(required = true) Double evaluation) {
        return ResponseEntity.ok(reportService.updateEvaluation(contentId, evaluation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(reportService.uploadImage(file));
    }
}

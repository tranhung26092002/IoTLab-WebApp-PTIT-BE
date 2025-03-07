package com.ptit.service.domain.services;

import com.ptit.service.app.dtos.ReportDTO;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.ReportResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Report;
import com.ptit.service.domain.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ReportService {
    ResponsePage<Report, ReportResponse> getReports(Pageable pageable);

    ReportResponse getReport(Long id);

    ReportResponse createReport(ReportDTO reportDTO);

    ReportResponse updateReport(Long id, ReportDTO reportDTO);

    MessageResponse deleteReport(Long id);

    String uploadImage(MultipartFile file);

    ReportResponse updateReportStatus(Long id, ReportStatus status);

    ReportResponse updateEvaluation(Long contentId, Double evaluation);

    ResponsePage<Report, ReportResponse> getReportsByStudentId(Long studentId, Pageable pageable);
}

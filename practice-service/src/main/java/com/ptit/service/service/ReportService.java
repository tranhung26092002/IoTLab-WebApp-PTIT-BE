package com.ptit.service.service;

import com.ptit.service.dto.ReportDTO;
import com.ptit.service.dto.ReportFilterDTO;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.ReportResponse;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.entity.Report;
import com.ptit.service.entity.enums.ReportStatus;
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

    ResponsePage<Report, ReportResponse> getReportsFilter(ReportFilterDTO reportFilterDTO, Pageable pageable);
}

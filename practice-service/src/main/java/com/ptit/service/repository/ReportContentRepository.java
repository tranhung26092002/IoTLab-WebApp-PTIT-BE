package com.ptit.service.repository;

import com.ptit.service.entity.ReportContent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportContentRepository extends JpaRepository<ReportContent, Long> {
    List<ReportContent> findByReportId(Long reportId);
}

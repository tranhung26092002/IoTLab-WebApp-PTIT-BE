package com.ptit.service.domain.migration;

import com.ptit.service.domain.entities.Practice;
import com.ptit.service.domain.entities.Report;
import com.ptit.service.domain.entities.StudentProgress;
import com.ptit.service.domain.enums.PracticeProgressStatus;
import com.ptit.service.domain.repository.PracticeRepository;
import com.ptit.service.domain.repository.ReportRepository;
import com.ptit.service.domain.repository.StudentProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class V1_2__Update_Existing_Student_Progress {

    private final ReportRepository reportRepository;
    private final StudentProgressRepository studentProgressRepository;
    private final PracticeRepository practiceRepository;

    @Transactional
    public void migrate() {
        // Lấy tất cả báo cáo đã hoàn thành
        List<Report> completedReports = reportRepository.findAll();

        // Lấy tất cả bài thực hành
        List<Practice> practices = practiceRepository.findAllByOrderByPracticeOrderAsc();
        Map<Long, Practice> practiceMap = practices.stream()
                .collect(Collectors.toMap(Practice::getId, practice -> practice));

        // Nhóm báo cáo theo sinh viên và bài thực hành
        Map<Long, Map<Long, Report>> studentPracticeReports = completedReports.stream()
                .collect(Collectors.groupingBy(
                        report -> report.getStudents().get(0).getId(),
                        Collectors.toMap(
                                report -> report.getPractice().getId(),
                                report -> report)));

        // Cập nhật tiến trình cho từng sinh viên
        studentPracticeReports.forEach((studentId, practiceReports) -> {
            List<StudentProgress> progressList = studentProgressRepository.findByStudentId(studentId);

            progressList.forEach(progress -> {
                Practice practice = progress.getPractice();
                if (practice == null)
                    return;

                // Nếu sinh viên đã có báo cáo cho bài này
                if (practiceReports.containsKey(practice.getId())) {
                    progress.setStatus(PracticeProgressStatus.COMPLETED);
                    progress.setCompletedAt(practiceReports.get(practice.getId()).getCreatedAt());
                }
                // Nếu là bài đầu tiên hoặc bài trước đó đã hoàn thành
                else if (practice.getPracticeOrder() == 1 ||
                        progressList.stream()
                                .anyMatch(p -> {
                                    Practice prevPractice = p.getPractice();
                                    return prevPractice != null &&
                                            prevPractice.getPracticeOrder() == practice.getPracticeOrder() - 1 &&
                                            p.getStatus() == PracticeProgressStatus.COMPLETED;
                                })) {
                    progress.setStatus(PracticeProgressStatus.UNLOCKED);
                }
                // Các bài còn lại
                else {
                    progress.setStatus(PracticeProgressStatus.LOCKED);
                }
            });

            studentProgressRepository.saveAll(progressList);
        });
    }
}
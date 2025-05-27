package com.ptit.service.service.Impl;

import com.ptit.service.entity.Practice;
import com.ptit.service.entity.Report;
import com.ptit.service.entity.Student;
import com.ptit.service.entity.StudentProgress;
import com.ptit.service.entity.enums.PracticeProgressStatus;
import com.ptit.service.repository.*;
import com.ptit.service.service.MigrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MigrationServiceImpl implements MigrationService {

    private final ReportRepository reportRepository;
    private final PracticeRepository practiceRepository;
    private final InstructorRepository instructorRepository;
    private final StudentProgressRepository studentProgressRepository;
    private final StudentRepository studentRepository;

    public MigrationServiceImpl(
            ReportRepository reportRepository,
            PracticeRepository practiceRepository,
            InstructorRepository instructorRepository,
            StudentProgressRepository studentProgressRepository,
            StudentRepository studentRepository) {
        this.reportRepository = reportRepository;
        this.practiceRepository = practiceRepository;
        this.instructorRepository = instructorRepository;
        this.studentProgressRepository = studentProgressRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public void updateExistingStudentProgress() {
        // Lấy tất cả sinh viên và bài thực hành
        List<Student> students = studentRepository.findAll();
        List<Practice> practices = practiceRepository.findAllByOrderByPracticeOrderAsc();

        // Lấy tất cả báo cáo
        List<Report> reports = reportRepository.findByIsDeletedFalse();

        // Với mỗi sinh viên
        for (Student student : students) {
            // Lấy danh sách báo cáo của sinh viên
            List<Report> studentReports = reports.stream()
                    .filter(report -> report.getStudents().contains(student))
                    .collect(Collectors.toList());

            // Lấy danh sách bài thực hành đã hoàn thành
            List<Long> completedPracticeIds = studentReports.stream()
                    .map(report -> report.getPractice().getId())
                    .collect(Collectors.toList());

            // Tạo StudentProgress cho tất cả bài thực hành
            for (Practice practice : practices) {
                // Kiểm tra xem sinh viên đã có StudentProgress cho bài này chưa
                StudentProgress progress = studentProgressRepository
                        .findByStudentIdAndPracticeId(student.getId(), practice.getId())
                        .orElseGet(() -> {
                            StudentProgress newProgress = new StudentProgress();
                            newProgress.setStudent(student);
                            newProgress.setPractice(practice);
                            newProgress.setCreatedAt(LocalDateTime.now());
                            return newProgress;
                        });

                // Nếu bài thực hành đã hoàn thành
                if (completedPracticeIds.contains(practice.getId())) {
                    progress.setStatus(PracticeProgressStatus.COMPLETED);
                    progress.setCompletedAt(LocalDateTime.now());
                } else {
                    // Nếu là bài đầu tiên hoặc bài trước đó đã hoàn thành
                    boolean isFirstPractice = practice.getPracticeOrder() == 1;
                    boolean previousPracticeCompleted = practice.getPracticeOrder() > 1 &&
                            completedPracticeIds.contains(practice.getId() - 1);

                    if (isFirstPractice || previousPracticeCompleted) {
                        progress.setStatus(PracticeProgressStatus.UNLOCKED);
                    } else {
                        progress.setStatus(PracticeProgressStatus.LOCKED);
                    }
                }

                studentProgressRepository.save(progress);
            }
        }
    }
}
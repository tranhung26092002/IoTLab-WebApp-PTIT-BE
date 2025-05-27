package com.ptit.service.service.Impl;

import com.ptit.service.entity.Practice;
import com.ptit.service.entity.StudentProgress;
import com.ptit.service.entity.Student;
import com.ptit.service.entity.enums.PracticeProgressStatus;
import com.ptit.service.service.StudentProgressService;
import com.ptit.service.repository.PracticeRepository;
import com.ptit.service.repository.StudentProgressRepository;
import com.ptit.service.repository.StudentRepository;
import com.ptit.service.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentProgressServiceImpl implements StudentProgressService {

    private final StudentProgressRepository studentProgressRepository;
    private final PracticeRepository practiceRepository;
    private final StudentRepository studentRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public List<StudentProgress> initializeStudentProgress(Long studentId) {
        List<Practice> practices = practiceRepository.findAllByOrderByPracticeOrderAsc();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        // Lấy danh sách các bài thực hành mà sinh viên đã nộp báo cáo
        List<Long> completedPracticeIds = reportRepository.findPracticeIdsByStudentId(studentId);

        return practices.stream().map(practice -> {
            StudentProgress progress = new StudentProgress();
            progress.setStudent(student);
            progress.setPractice(practice);

            // Mở khóa nếu:
            // 1. Là bài đầu tiên
            // 2. Hoặc sinh viên đã nộp báo cáo cho bài này
            // 3. Hoặc sinh viên đã nộp báo cáo cho bài trước đó (để đảm bảo tính liên tục)
            boolean isFirstPractice = practice.getPracticeOrder() == 1;
            boolean hasReportForThisPractice = completedPracticeIds.contains(practice.getId());
            boolean hasReportForPreviousPractice = practice.getPracticeOrder() > 1 &&
                    completedPracticeIds.contains(practice.getId() - 1);

            progress.setStatus(
                    (isFirstPractice || hasReportForThisPractice || hasReportForPreviousPractice)
                            ? PracticeProgressStatus.UNLOCKED
                            : PracticeProgressStatus.LOCKED);

            return studentProgressRepository.save(progress);
        }).collect(Collectors.toList());
    }

    @Override
    public List<StudentProgress> getStudentProgress(Long studentId) {
        return studentProgressRepository.findByStudentIdOrderByPracticeOrderAsc(studentId);
    }

    @Override
    public StudentProgress getPracticeProgress(Long studentId, Long practiceId) {
        return studentProgressRepository.findByStudentIdAndPracticeId(studentId, practiceId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tiến trình học tập"));
    }

    @Override
    @Transactional
    public StudentProgress startPractice(Long studentId, Long practiceId) {
        StudentProgress progress = getPracticeProgress(studentId, practiceId);

        if (!canStartPractice(studentId, practiceId)) {
            return progress;
        }

        progress.setStatus(PracticeProgressStatus.IN_PROGRESS);
        progress.setStartedAt(LocalDateTime.now());
        return studentProgressRepository.save(progress);
    }

    @Override
    @Transactional
    public StudentProgress completePractice(Long studentId, Long practiceId, Double score, String comment) {
        StudentProgress progress = getPracticeProgress(studentId, practiceId);

        if (progress.getStatus() != PracticeProgressStatus.IN_PROGRESS) {
            throw new IllegalStateException("Bài thực hành chưa được bắt đầu");
        }

        progress.setStatus(PracticeProgressStatus.COMPLETED);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setScore(score);
        progress.setComment(comment);

        // Mở khóa bài tiếp theo
        studentProgressRepository.findNextUnlockedPractice(studentId)
                .ifPresent(nextProgress -> {
                    nextProgress.setStatus(PracticeProgressStatus.UNLOCKED);
                    studentProgressRepository.save(nextProgress);
                });

        return studentProgressRepository.save(progress);
    }

    @Override
    @Transactional
    public StudentProgress updatePracticeScore(Long studentId, Long practiceId, Double score, String comment) {
        StudentProgress progress = getPracticeProgress(studentId, practiceId);
        progress.setScore(score);
        progress.setComment(comment);
        return studentProgressRepository.save(progress);
    }

    @Override
    public boolean canStartPractice(Long studentId, Long practiceId) {
        StudentProgress progress = getPracticeProgress(studentId, practiceId);

        // Kiểm tra trạng thái hiện tại
        if (progress.getStatus() != PracticeProgressStatus.UNLOCKED) {
            return false;
        }

        // Kiểm tra xem có bài nào đang trong trạng thái IN_PROGRESS không
        return !studentProgressRepository.findByStudentIdAndStatus(studentId, PracticeProgressStatus.IN_PROGRESS)
                .isPresent();
    }

    @Override
    public boolean hasCompletedAllPractices(Long studentId) {
        return studentProgressRepository.hasCompletedAllPractices(studentId);
    }

    @Override
    public double calculateCompletionRate(Long studentId) {
        List<StudentProgress> progressList = studentProgressRepository
                .findByStudentIdOrderByPracticeOrderAsc(studentId);

        if (progressList.isEmpty()) {
            return 0.0;
        }

        long completedCount = progressList.stream()
                .filter(progress -> progress.getStatus() == PracticeProgressStatus.COMPLETED)
                .count();

        return (double) completedCount / progressList.size() * 100;
    }
}
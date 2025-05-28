package com.ptit.service.service.Impl;

import com.ptit.service.dto.ReportContentDTO;
import com.ptit.service.dto.ReportDTO;
import com.ptit.service.dto.ReportFilterDTO;
import com.ptit.service.dto.StudentDTO;
import com.ptit.service.repository.*;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.ReportResponse;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.entity.Instructor;
import com.ptit.service.entity.Report;
import com.ptit.service.entity.ReportContent;
import com.ptit.service.entity.Student;
import com.ptit.service.entity.StudentProgress;
import com.ptit.service.entity.enums.PracticeProgressStatus;
import com.ptit.service.entity.enums.ReportStatus;
import com.ptit.service.service.ReportService;
import com.ptit.service.service.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final FileService fileService;
    private final ModelMapper mapper;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final ReportContentRepository reportContentRepository;
    private final PracticeRepository practiceRepository;
    private final StudentProgressRepository studentProgressRepository;

    @Override
    public ResponsePage<Report, ReportResponse> getReports(Pageable pageable) {
        Page<Report> reports = reportRepository.findAll(pageable);

        Page<ReportResponse> response = reports.map(this::convertToResponse);

        return new ResponsePage<>(response);
    }

    private ReportResponse convertToResponse(Report report) {
        ReportResponse response = mapper.map(report, ReportResponse.class);
        response.setStudents(report.getStudents().stream()
                .map(student -> new StudentDTO(student.getId(), student.getUserId(), student.getName(),
                        student.getStudentCode()))
                .collect(Collectors.toList()));
        response.setReportContents(report.getPracticeContents().stream()
                .map(reportContent -> new ReportContentDTO(reportContent.getId(), reportContent.getUserId(),
                        reportContent.getContent(), reportContent.getPerformer(), reportContent.getImageUrl(),
                        reportContent.getEvaluation()))
                .collect(Collectors.toList()));

        return response;
    }

    public ReportResponse getReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return mapper.map(report, ReportResponse.class);
    }

    @Override
    @Transactional
    public ReportResponse createReport(ReportDTO reportDTO) {
        Report report = mapper.map(reportDTO, Report.class);

        // check if instructor exists or not create new instructor
        Instructor instructor = instructorRepository.findByUserId(reportDTO.getInstructor().getUserId())
                .orElseGet(() -> {
                    Instructor newInstructor = new Instructor();
                    newInstructor.setUserId(reportDTO.getInstructor().getUserId());
                    newInstructor.setName(reportDTO.getInstructor().getName());
                    return instructorRepository.save(newInstructor);
                });
        report.setInstructor(instructor);

        report.setPractice(practiceRepository.findById(reportDTO.getPracticeId())
                .orElseThrow(() -> new ResourceNotFoundException("Practice not found")));

        // check if student exists or not create new student
        // Truy vấn tất cả sinh viên trước thay vì gọi findByUserId() nhiều lần
        Set<Long> userIds = reportDTO.getStudents()
                .stream()
                .map(StudentDTO::getUserId)
                .collect(Collectors.toSet());
        // Truy vấn tất cả sinh viên theo userId
        List<Student> existingStudents = studentRepository.findByUserIdIn(userIds);

        // Tạo map userId -> Student
        Map<Long, Student> studentMap = existingStudents.stream()
                .collect(Collectors.toMap(Student::getUserId, student -> student));

        // Tạo danh sách sinh viên mới
        List<Student> students = reportDTO.getStudents()
                .stream()
                .map(studentDTO -> studentMap.computeIfAbsent(studentDTO.getUserId(), code -> {
                    Student newStudent = new Student();
                    newStudent.setUserId(studentDTO.getUserId());
                    newStudent.setName(studentDTO.getName());
                    newStudent.setStudentCode(studentDTO.getStudentCode());
                    return studentRepository.save(newStudent);
                }))
                .collect(Collectors.toList());
        report.setStudents(students);

        // Lưu báo cáo
        final Report savedReport = reportRepository.save(report);

        // Tạo StudentProgress cho mỗi sinh viên nếu chưa có
        for (Student student : students) {
            studentProgressRepository.findByStudentIdAndPracticeId(student.getId(), savedReport.getPractice().getId())
                    .orElseGet(() -> {
                        StudentProgress progress = new StudentProgress();
                        progress.setStudent(student);
                        progress.setPractice(savedReport.getPractice());
                        // Nếu là bài đầu tiên thì mở khóa, ngược lại thì khóa
                        progress.setStatus(
                                savedReport.getPractice().getPracticeOrder() == 1 ? PracticeProgressStatus.UNLOCKED
                                        : PracticeProgressStatus.LOCKED);
                        return studentProgressRepository.save(progress);
                    });
        }

        // Nếu báo cáo được tạo với trạng thái SUBMITTED, cập nhật trạng thái tiến trình
        // thực hành
        if (report.getStatus() == ReportStatus.SUBMITTED) {
            for (Student student : students) {
                studentProgressRepository
                        .findByStudentIdAndPracticeId(student.getId(), savedReport.getPractice().getId())
                        .ifPresent(progress -> {
                            progress.setStatus(PracticeProgressStatus.COMPLETED);
                            progress.setCompletedAt(LocalDateTime.now());
                            studentProgressRepository.save(progress);

                            // Mở khóa bài thực hành tiếp theo
                            practiceRepository.findByPracticeOrder(savedReport.getPractice().getPracticeOrder() + 1)
                                    .ifPresent(nextPractice -> {
                                        studentProgressRepository
                                                .findByStudentIdAndPracticeId(student.getId(), nextPractice.getId())
                                                .ifPresentOrElse(
                                                        nextProgress -> {
                                                            nextProgress.setStatus(PracticeProgressStatus.UNLOCKED);
                                                            studentProgressRepository.save(nextProgress);
                                                        },
                                                        () -> {
                                                            StudentProgress nextProgress = new StudentProgress();
                                                            nextProgress.setStudent(student);
                                                            nextProgress.setPractice(nextPractice);
                                                            nextProgress.setStatus(PracticeProgressStatus.UNLOCKED);
                                                            studentProgressRepository.save(nextProgress);
                                                        });
                                    });
                        });
            }
        }

        // Lưu danh sách sinh viên thực hiện báo cáo
        List<ReportContent> reportContents = reportDTO.getReportContents()
                .stream()
                .map(reportContentDTO -> {
                    ReportContent reportContent = new ReportContent();
                    reportContent.setReport(savedReport);
                    reportContent.setUserId(reportContentDTO.getUserId());
                    reportContent.setContent(reportContentDTO.getContent());
                    reportContent.setPerformer(reportContentDTO.getPerformer());
                    reportContent.setImageUrl(reportContentDTO.getImageUrl());
                    reportContent.setEvaluation(reportContentDTO.getEvaluation());
                    return reportContent;
                })
                .collect(Collectors.toList());

        reportContentRepository.saveAll(reportContents);

        return convertToResponse(savedReport, students, reportContents);
    }

    @Override
    public ReportResponse updateReport(Long id, ReportDTO reportDTO) {
        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        mapper.map(reportDTO, existingReport);
        return mapper.map(reportRepository.save(existingReport), ReportResponse.class);
    }

    @Override
    public MessageResponse deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setIsDeleted(true);
        reportRepository.save(report);

        return new MessageResponse("Report deleted successfully");
    }

    public String uploadImage(MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @Override
    public ReportResponse updateReportStatus(Long id, ReportStatus newStatus) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        ReportStatus currentStatus = report.getStatus();

        // Kiểm tra trạng thái hợp lệ
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        report.setStatus(newStatus);
        Report savedReport = reportRepository.save(report);

        // Nếu báo cáo được nộp (SUBMITTED), cập nhật trạng thái tiến trình thực hành
        // của sinh viên
        if (newStatus == ReportStatus.SUBMITTED) {
            for (Student student : report.getStudents()) {
                studentProgressRepository.findByStudentIdAndPracticeId(student.getId(), report.getPractice().getId())
                        .ifPresent(progress -> {
                            progress.setStatus(PracticeProgressStatus.COMPLETED);
                            progress.setCompletedAt(LocalDateTime.now());
                            studentProgressRepository.save(progress);

                            // Mở khóa bài thực hành tiếp theo
                            practiceRepository.findByPracticeOrder(report.getPractice().getPracticeOrder() + 1)
                                    .ifPresent(nextPractice -> {
                                        studentProgressRepository
                                                .findByStudentIdAndPracticeId(student.getId(), nextPractice.getId())
                                                .ifPresentOrElse(
                                                        nextProgress -> {
                                                            nextProgress.setStatus(PracticeProgressStatus.UNLOCKED);
                                                            studentProgressRepository.save(nextProgress);
                                                        },
                                                        () -> {
                                                            StudentProgress nextProgress = new StudentProgress();
                                                            nextProgress.setStudent(student);
                                                            nextProgress.setPractice(nextPractice);
                                                            nextProgress.setStatus(PracticeProgressStatus.UNLOCKED);
                                                            studentProgressRepository.save(nextProgress);
                                                        });
                                    });
                        });
            }
        }

        return mapper.map(savedReport, ReportResponse.class);
    }

    /**
     * Kiểm tra xem trạng thái có thể chuyển đổi hợp lệ hay không (Java 8)
     */
    private boolean isValidStatusTransition(ReportStatus current, ReportStatus next) {
        if (current == ReportStatus.DRAFT && next == ReportStatus.SUBMITTED) {
            return true;
        }
        if (current == ReportStatus.SUBMITTED && next == ReportStatus.PENDING) {
            return true;
        }
        if (current == ReportStatus.PENDING && (next == ReportStatus.APPROVED || next == ReportStatus.REJECTED)) {
            return true;
        }
        return false;
    }

    @Override
    public ReportResponse updateEvaluation(Long contentId, Double evaluation) {
        ReportContent reportContent = reportContentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Report content not found"));

        reportContent.setEvaluation(evaluation);
        reportContentRepository.save(reportContent);

        Report report = reportContent.getReport();

        return mapper.map(report, ReportResponse.class);
    }

    @Override
    public ResponsePage<Report, ReportResponse> getReportsByStudentId(Long studentId, Pageable pageable) {
        Page<Report> reports = reportRepository.findByStudentsId(studentId, pageable);

        Page<ReportResponse> response = reports.map(this::convertToResponse);

        return new ResponsePage<>(response);
    }

    @Override
    public ResponsePage<Report, ReportResponse> getReportsFilter(ReportFilterDTO reportFilterDTO, Pageable pageable) {
        Sort.Direction direction = Sort.Direction.ASC;

        if (reportFilterDTO.getSortOrder() != null && reportFilterDTO.getSortOrder().equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Sort sort = Sort.by(direction, reportFilterDTO.getSortField());
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Report> reportPage = reportRepository.filterReports(
                reportFilterDTO,
                pageRequest);

        Page<ReportResponse> response = reportPage.map(this::convertToResponse);

        return new ResponsePage<>(response);
    }

    private ReportResponse convertToResponse(Report report, List<Student> students,
            List<ReportContent> reportContents) {
        ReportResponse response = mapper.map(report, ReportResponse.class);
        response.setStudents(students.stream()
                .map(student -> new StudentDTO(student.getId(), student.getUserId(), student.getName(),
                        student.getStudentCode()))
                .collect(Collectors.toList()));
        response.setReportContents(reportContents.stream()
                .map(reportContent -> new ReportContentDTO(reportContent.getId(), reportContent.getUserId(),
                        reportContent.getContent(), reportContent.getPerformer(), reportContent.getImageUrl(),
                        reportContent.getEvaluation()))
                .collect(Collectors.toList()));

        return response;
    }
}

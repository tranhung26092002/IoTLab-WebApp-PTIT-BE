package com.ptit.service.domain.service;

import com.ptit.service.app.dtos.UserDTO;
import com.ptit.service.domain.entities.Student;
import com.ptit.service.domain.entities.StudentProgress;
import com.ptit.service.domain.entities.UserLoginNotification;
import com.ptit.service.domain.enums.PracticeProgressStatus;
import com.ptit.service.domain.repository.PracticeRepository;
import com.ptit.service.domain.repository.StudentProgressRepository;
import com.ptit.service.domain.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationService {

    private final StudentService studentService;
    private final StudentProgressService studentProgressService;
    private final StudentRepository studentRepository;
    private final PracticeRepository practiceRepository;
    private final StudentProgressRepository studentProgressRepository;

    @Value("${user.login.queue}")
    private String userLoginQueue;

    @Value("${user.created.queue}")
    private String userCreatedQueue;

    /**
     * Xử lý thông báo đăng nhập của user
     */
    @RabbitListener(queues = "${user.login.queue}")
    @Transactional
    public void handleUserLogin(UserLoginNotification userLoginNotification) {
        log.info("Nhận thông báo đăng nhập từ user: {}", userLoginNotification.getUserName());

        // Tìm student dựa trên userId
        Optional<Student> studentOpt = studentService.findByUserId(userLoginNotification.getId());

        if (studentOpt.isEmpty()) {
            // Nếu chưa có student, tạo mới
            Student newStudent = studentService.createStudent(
                    userLoginNotification.getId(),
                    userLoginNotification.getFullName(),
                    userLoginNotification.getUserName() // Sử dụng userName làm studentCode
            );

            // Khởi tạo tiến trình học tập cho student mới
            studentProgressService.initializeStudentProgress(newStudent.getId());
            log.info("Đã tạo student mới và khởi tạo tiến trình học tập: {}", newStudent.getId());
        } else {
            // Nếu đã có student, kiểm tra và cập nhật thông tin nếu cần
            Student student = studentOpt.get();
            if (!student.getName().equals(userLoginNotification.getFullName())) {
                student.setName(userLoginNotification.getFullName());
                studentService.updateStudent(student);
                log.info("Đã cập nhật thông tin student: {}", student.getId());
            }

            // Kiểm tra xem đã có tiến trình học tập chưa
            if (studentProgressService.getStudentProgress(student.getId()).isEmpty()) {
                studentProgressService.initializeStudentProgress(student.getId());
                log.info("Đã khởi tạo tiến trình học tập cho student: {}", student.getId());
            }
        }
    }

    /**
     * Xử lý thông báo tạo user mới
     */
    @RabbitListener(queues = "${user.created.queue}")
    @Transactional
    public void handleUserCreated(UserDTO userDTO) {
        log.info("Nhận thông báo tạo user mới từ user-service: {}", userDTO);

        try {
            // Validate dữ liệu
            if (userDTO == null || userDTO.getId() == null || userDTO.getRole() == null) {
                log.error("Dữ liệu user không hợp lệ: {}", userDTO);
                return;
            }

            // Chỉ xử lý nếu user là sinh viên
            if ("STUDENT".equals(userDTO.getRole())) {
                // Kiểm tra xem sinh viên đã tồn tại chưa
                if (!studentRepository.existsByUserId(userDTO.getId())) {
                    try {
                        // Tạo sinh viên mới trong practice-service
                        final Student student = new Student();
                        student.setUserId(userDTO.getId());
                        student.setName(userDTO.getFullName());
                        student.setStudentCode(userDTO.getStudentCode());
                        final Student savedStudent = studentRepository.save(student);

                        // Lấy tất cả bài thực hành hiện có
                        List<StudentProgress> progressList = practiceRepository.findAllByOrderByPracticeOrderAsc()
                                .stream()
                                .map(practice -> {
                                    StudentProgress progress = new StudentProgress();
                                    progress.setStudent(savedStudent);
                                    progress.setPractice(practice);
                                    // Bài đầu tiên được mở khóa, các bài còn lại bị khóa
                                    progress.setStatus(
                                            practice.getPracticeOrder() == 1 ? PracticeProgressStatus.UNLOCKED
                                                    : PracticeProgressStatus.LOCKED);
                                    return progress;
                                })
                                .collect(Collectors.toList());

                        // Lưu tất cả tiến trình
                        studentProgressRepository.saveAll(progressList);

                        log.info("Đã tạo tiến trình học tập cho sinh viên: {}", savedStudent.getId());
                    } catch (Exception e) {
                        log.error("Lỗi khi tạo sinh viên và tiến trình học tập: {}", e.getMessage(), e);
                        throw e;
                    }
                } else {
                    log.info("Sinh viên đã tồn tại trong practice-service: {}", userDTO.getId());
                }
            } else {
                log.info("User không phải là sinh viên, bỏ qua: {}", userDTO.getId());
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý thông báo tạo user: {}", e.getMessage(), e);
            throw e;
        }
    }
}
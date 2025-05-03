package com.ptit.service.domain.service;

import com.ptit.service.domain.entities.Student;
import java.util.Optional;

public interface StudentService {
    // Tìm student theo userId
    Optional<Student> findByUserId(Long userId);

    // Tạo student mới từ userId và thông tin cơ bản
    Student createStudent(Long userId, String name, String studentCode);

    // Cập nhật thông tin student
    Student updateStudent(Student student);
}
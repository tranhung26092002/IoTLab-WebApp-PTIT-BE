package com.ptit.service.service;

import com.ptit.service.entity.Student;

public interface StudentService {
    // Tạo student mới với thông tin cơ bản
    Student createStudent(Long id, String name, String studentCode);
}
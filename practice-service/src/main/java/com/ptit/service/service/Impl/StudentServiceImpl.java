package com.ptit.service.service.Impl;

import com.ptit.service.entity.Student;
import com.ptit.service.repository.StudentRepository;
import com.ptit.service.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public Student createStudent(Long id, String name, String studentCode) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setStudentCode(studentCode);
        return studentRepository.save(student);
    }
}
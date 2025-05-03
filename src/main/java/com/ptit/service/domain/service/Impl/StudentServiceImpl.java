package com.ptit.service.domain.service.Impl;

import com.ptit.service.domain.entities.Student;
import com.ptit.service.domain.repository.StudentRepository;
import com.ptit.service.domain.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public Optional<Student> findByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Student createStudent(Long userId, String name, String studentCode) {
        Student student = new Student();
        student.setUserId(userId);
        student.setName(name);
        student.setStudentCode(studentCode);
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }
}
package com.ptit.service.repository;

import com.ptit.service.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByIdIn(Set<Long> ids);

    boolean existsById(Long id);
}

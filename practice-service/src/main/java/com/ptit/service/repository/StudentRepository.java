package com.ptit.service.repository;

import com.ptit.service.entity.Student;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByUserIdIn(Set<Long> userIds);

    boolean existsByUserId(Long userId);

    Optional<Student> findByUserId(Long userId);
}

package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.Student;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {


    List<Student> findByUserIdIn(Set<Long> userIds);
}

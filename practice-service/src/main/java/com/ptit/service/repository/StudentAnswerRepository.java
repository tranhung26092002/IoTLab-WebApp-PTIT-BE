package com.ptit.service.repository;

import com.ptit.service.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByStudentExamId(Long studentExamId);

    List<StudentAnswer> findByQuestionId(Long questionId);
}
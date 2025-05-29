package com.ptit.service.repository;

import com.ptit.service.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByStudentExamId(Long studentExamId);

    @Query("SELECT sa FROM StudentAnswer sa " +
           "WHERE sa.studentExam.id = :studentExamId AND sa.question.id = :questionId")
    Optional<StudentAnswer> findByStudentExamIdAndQuestionId(
        @Param("studentExamId") Long studentExamId,
        @Param("questionId") Long questionId
    );

    @Query("SELECT COUNT(sa) FROM StudentAnswer sa " +
           "WHERE sa.studentExam.id = :studentExamId AND sa.score > 0")
    Long countCorrectAnswers(@Param("studentExamId") Long studentExamId);
} 
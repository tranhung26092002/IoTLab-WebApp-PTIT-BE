package com.ptit.service.repository;

import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.enums.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {
    @Query("SELECT se FROM StudentExam se WHERE se.student.id = :studentId")
    List<StudentExam> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT DISTINCT se FROM StudentExam se " +
            "LEFT JOIN FETCH se.answers " +
            "WHERE se.id = :id")
    StudentExam findByIdWithAnswers(@Param("id") Long id);

    @Query("SELECT se FROM StudentExam se " +
           "WHERE se.student.id = :studentId AND se.exam.id = :examId")
    StudentExam findByStudentIdAndExamId(
        @Param("studentId") Long studentId,
        @Param("examId") Long examId
    );
}
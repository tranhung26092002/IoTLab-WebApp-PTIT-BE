package com.ptit.service.repository;

import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {
    @Query("SELECT se FROM StudentExam se WHERE se.studentId = :studentId")
    List<StudentExam> findByStudentId(@Param("studentId") String studentId);

    @Query("SELECT se FROM StudentExam se WHERE se.exam.id = :examId")
    List<StudentExam> findByExamId(@Param("examId") Long examId);

    @Query("SELECT se FROM StudentExam se WHERE se.status = :status")
    List<StudentExam> findByStatus(@Param("status") ExamStatus status);

    @Query("SELECT DISTINCT se FROM StudentExam se " +
            "LEFT JOIN FETCH se.answers " +
            "WHERE se.id = :id")
    StudentExam findByIdWithAnswers(@Param("id") Long id);

    @Query("SELECT DISTINCT se FROM StudentExam se " +
            "LEFT JOIN FETCH se.answers " +
            "WHERE se.studentId = :studentId")
    List<StudentExam> findByStudentIdWithAnswers(@Param("studentId") String studentId);

    @Query("SELECT DISTINCT se FROM StudentExam se " +
            "LEFT JOIN FETCH se.answers " +
            "WHERE se.exam.id = :examId")
    List<StudentExam> findByExamIdWithAnswers(@Param("examId") Long examId);
}
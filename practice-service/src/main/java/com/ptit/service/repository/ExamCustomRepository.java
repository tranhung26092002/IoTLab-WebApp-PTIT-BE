package com.ptit.service.repository;

import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.entity.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamCustomRepository extends JpaRepository<StudentExam, Long> {

    @Query("SELECT new com.ptit.service.dto.StudentExamResult(" +
            "se.id, se.studentId, se.score, " +
            "COUNT(CASE WHEN sa.score > 0 THEN 1 END)) " +
            "FROM StudentExam se " +
            "LEFT JOIN StudentAnswer sa ON se.id = sa.studentExam.id " +
            "WHERE se.exam.id = :examId " +
            "GROUP BY se.id, se.studentId, se.score")
    List<StudentExamResult> getStudentExamResults(@Param("examId") Long examId);
}
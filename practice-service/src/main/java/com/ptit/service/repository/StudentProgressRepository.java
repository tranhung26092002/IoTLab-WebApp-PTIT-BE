package com.ptit.service.repository;

import com.ptit.service.entity.StudentProgress;
import com.ptit.service.entity.enums.PracticeProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {

        // Lấy tất cả tiến trình của một sinh viên
        @Query("SELECT sp FROM StudentProgress sp WHERE sp.student.id = :studentId ORDER BY sp.practice.practiceOrder ASC")
        List<StudentProgress> findByStudentIdOrderByPracticeOrderAsc(@Param("studentId") Long studentId);

        // Lấy tiến trình của một bài thực hành cụ thể của sinh viên
        Optional<StudentProgress> findByStudentIdAndPracticeId(Long studentId, Long practiceId);

        // Lấy bài thực hành đang thực hiện của sinh viên
        Optional<StudentProgress> findByStudentIdAndStatus(Long studentId, PracticeProgressStatus status);

        // Lấy bài thực hành tiếp theo cần mở khóa
        @Query("SELECT sp FROM StudentProgress sp WHERE sp.student.id = :studentId " +
                        "AND sp.practice.practiceOrder = (SELECT MIN(p.practiceOrder) FROM Practice p " +
                        "WHERE p.practiceOrder > (SELECT COALESCE(MAX(sp2.practice.practiceOrder), 0) " +
                        "FROM StudentProgress sp2 WHERE sp2.student.id = :studentId " +
                        "AND sp2.status = 'COMPLETED'))")
        Optional<StudentProgress> findNextUnlockedPractice(@Param("studentId") Long studentId);

        // Kiểm tra xem sinh viên đã hoàn thành tất cả bài thực hành chưa
        @Query("SELECT CASE WHEN COUNT(p) = (SELECT COUNT(p2) FROM StudentProgress p2 WHERE p2.student.id = :studentId) AND COUNT(p) > 0 THEN true ELSE false END FROM StudentProgress p WHERE p.student.id = :studentId AND p.status = 'COMPLETED'")
        boolean hasCompletedAllPractices(Long studentId);

        List<StudentProgress> findByStudentId(Long studentId);
}
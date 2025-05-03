package com.ptit.service.domain.service;

import com.ptit.service.domain.entities.StudentProgress;

import java.util.List;

public interface StudentProgressService {

    // Khởi tạo tiến trình học tập cho sinh viên mới
    List<StudentProgress> initializeStudentProgress(Long studentId);

    // Lấy tất cả tiến trình của sinh viên
    List<StudentProgress> getStudentProgress(Long studentId);

    // Lấy tiến trình của một bài thực hành cụ thể
    StudentProgress getPracticeProgress(Long studentId, Long practiceId);

    // Bắt đầu làm bài thực hành
    StudentProgress startPractice(Long studentId, Long practiceId);

    // Hoàn thành bài thực hành
    StudentProgress completePractice(Long studentId, Long practiceId, Double score, String comment);

    // Cập nhật điểm và ghi chú cho bài thực hành
    StudentProgress updatePracticeScore(Long studentId, Long practiceId, Double score, String comment);

    // Kiểm tra xem sinh viên có thể bắt đầu bài thực hành không
    boolean canStartPractice(Long studentId, Long practiceId);

    // Kiểm tra xem sinh viên đã hoàn thành tất cả bài thực hành chưa
    boolean hasCompletedAllPractices(Long studentId);

    // Tính tỷ lệ hoàn thành của sinh viên
    double calculateCompletionRate(Long studentId);
}
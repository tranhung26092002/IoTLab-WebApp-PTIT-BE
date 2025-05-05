package com.ptit.service.domain.service;

import com.ptit.service.domain.entities.StudentProgress;

import java.util.List;

public interface StudentProgressService {

    // Khởi tạo tiến trình học tập cho sinh viên mới
    List<StudentProgress> initializeStudentProgress(Long userId);

    // Lấy tất cả tiến trình của sinh viên
    List<StudentProgress> getStudentProgress(Long userId);

    // Lấy tiến trình của một bài thực hành cụ thể
    StudentProgress getPracticeProgress(Long userId, Long practiceId);

    // Bắt đầu làm bài thực hành
    StudentProgress startPractice(Long userId, Long practiceId);

    // Hoàn thành bài thực hành
    StudentProgress completePractice(Long userId, Long practiceId, Double score, String comment);

    // Cập nhật điểm và ghi chú cho bài thực hành
    StudentProgress updatePracticeScore(Long userId, Long practiceId, Double score, String comment);

    // Kiểm tra xem sinh viên có thể bắt đầu bài thực hành không
    boolean canStartPractice(Long userId, Long practiceId);

    // Kiểm tra xem sinh viên đã hoàn thành tất cả bài thực hành chưa
    boolean hasCompletedAllPractices(Long userId);

    // Tính tỷ lệ hoàn thành của sinh viên
    double calculateCompletionRate(Long userId);
}
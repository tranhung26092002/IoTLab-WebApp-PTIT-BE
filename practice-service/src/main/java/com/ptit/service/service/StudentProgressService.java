package com.ptit.service.service;

import com.ptit.service.dto.StudentProgressDTO;
import com.ptit.service.entity.StudentProgress;

import java.util.List;

public interface StudentProgressService {

    // Khởi tạo tiến trình học tập cho sinh viên mới
    List<StudentProgress> initializeStudentProgress(Long userId);

    // Lấy tất cả tiến trình của sinh viên
    List<StudentProgressDTO> getStudentProgress(Long userId);

    // Lấy tiến trình của một bài thực hành cụ thể
    StudentProgressDTO getPracticeProgress(Long userId, Long practiceId);

    // Bắt đầu làm bài thực hành
    StudentProgressDTO startPractice(Long userId, Long practiceId);

    // Hoàn thành bài thực hành
    StudentProgressDTO completePractice(Long userId, Long practiceId, Double score, String comment);

    // Cập nhật điểm và ghi chú cho bài thực hành
    StudentProgressDTO updatePracticeScore(Long userId, Long practiceId, Double score, String comment);

    // Kiểm tra xem sinh viên có thể bắt đầu bài thực hành không
    boolean canStartPractice(Long userId, Long practiceId);

    // Kiểm tra xem sinh viên đã hoàn thành tất cả bài thực hành chưa
    boolean hasCompletedAllPractices(Long userId);

    // Tính tỷ lệ hoàn thành của sinh viên
    double calculateCompletionRate(Long userId);
}
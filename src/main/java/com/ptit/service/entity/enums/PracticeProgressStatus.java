package com.ptit.service.entity.enums;

public enum PracticeProgressStatus {
    LOCKED, // Bài thực hành đang bị khóa
    UNLOCKED, // Bài thực hành đã được mở khóa nhưng chưa bắt đầu
    IN_PROGRESS, // Đang thực hiện bài thực hành
    COMPLETED // Đã hoàn thành bài thực hành
}
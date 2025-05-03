package com.ptit.service.app.dtos;

import com.ptit.service.domain.enums.PracticeProgressStatus;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentProgressDTO {
    private Long id;
    private Long studentId;
    private Long practiceId;
    private PracticeProgressStatus status;
    private Double score;
    private String comment;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
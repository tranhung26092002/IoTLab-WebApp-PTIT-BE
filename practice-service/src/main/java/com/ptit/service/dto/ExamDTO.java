package com.ptit.service.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamDTO {
    private Long id;
    private String title;
    private String description;
    private List<ExamQuestionDTO> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
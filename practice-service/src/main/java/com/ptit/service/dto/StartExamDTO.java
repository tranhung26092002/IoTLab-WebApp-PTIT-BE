package com.ptit.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StartExamDTO {
    @NotNull(message = "Exam ID is required")
    private Long examId;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    private LocalDateTime startTime;
} 
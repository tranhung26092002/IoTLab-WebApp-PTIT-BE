package com.ptit.service.dto;

import com.ptit.service.entity.ExamStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentExamDTO {
    private Long id;
    private String studentId;
    private Long examId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ExamStatus status;
    private Double score;
}
package com.ptit.service.dto;

import com.ptit.service.entity.enums.ExamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentExamDTO {
    private Long id;
    private StudentDTO student;
    private ExamDTO exam;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ExamStatus status;
    private Double score;
    private List<StudentAnswerDTO> answers;
}

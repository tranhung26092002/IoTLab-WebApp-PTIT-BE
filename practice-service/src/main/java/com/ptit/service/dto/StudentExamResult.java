package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentExamResult {
    private Long id;
    private String studentId;
    private Double score;
    private Integer correctAnswers;
}
package com.ptit.service.dto;

import lombok.Data;

@Data
public class StudentAnswerDTO {
    private Long id;
    private Long studentExamId;
    private Long questionId;
    private String answer;
    private Double score;
}
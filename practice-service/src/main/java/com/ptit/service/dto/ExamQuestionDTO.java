package com.ptit.service.dto;

import lombok.Data;

@Data
public class ExamQuestionDTO {
    private Long id;
    private Long examId;
    private QuestionDTO question;
    private Integer order;
}
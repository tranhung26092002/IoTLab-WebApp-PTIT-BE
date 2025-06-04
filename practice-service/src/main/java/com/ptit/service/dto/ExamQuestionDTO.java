package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionDTO {
    private Long id;
    private QuestionDTO question;
    private Integer order;
}
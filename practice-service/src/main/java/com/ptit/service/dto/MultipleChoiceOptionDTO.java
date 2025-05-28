package com.ptit.service.dto;

import lombok.Data;

@Data
public class MultipleChoiceOptionDTO {
    private Long id;
    private String option;
    private String content;
    private boolean isCorrect;
}
package com.ptit.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private List<QuestionResponse> questions;

    @Data
    public static class QuestionResponse {
        private Long id;
        private String content;
        private String type;
        private List<String> options;
        private Integer score;
    }
} 
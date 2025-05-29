package com.ptit.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class StudentAnswerDTO {
    @NotNull(message = "Question ID is required")
    private Long questionId;
    
    // Cho câu trả lời tự luận
    private String essayAnswer;
    
    // Cho câu trả lời trắc nghiệm (A, B, C, D)
    private String selectedOption;
    
    // Cho câu trả lời tự luận có hình ảnh
    private List<String> imageUrls;
    
    private Double score;
}
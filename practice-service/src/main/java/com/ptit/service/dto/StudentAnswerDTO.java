package com.ptit.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class StudentAnswerDTO {
    @NotNull(message = "Question ID is required")
    private Long questionId;
    
    // Loại câu hỏi: MULTIPLE_CHOICE hoặc ESSAY
    @NotNull(message = "Question type is required")
    private String questionType;
    
    // Cho câu trả lời trắc nghiệm (A, B, C, D)
    private String selectedOption;
    
    // Cho câu trả lời tự luận
    private String essayAnswer;
    
    // Cho câu trả lời tự luận có hình ảnh (tối đa 3 ảnh)
    private List<String> imageUrls;
    
    private Double score;
}
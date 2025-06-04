package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
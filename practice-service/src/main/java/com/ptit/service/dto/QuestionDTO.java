package com.ptit.service.dto;

import com.ptit.service.entity.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private QuestionType type;
    private String content;
    private List<MultipleChoiceOptionDTO> options;
    private Double score;
}
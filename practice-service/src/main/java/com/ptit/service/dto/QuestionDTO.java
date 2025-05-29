package com.ptit.service.dto;

import com.ptit.service.entity.enums.QuestionType;
import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private QuestionType type;
    private String content;
    private List<MultipleChoiceOptionDTO> options;
}
package com.ptit.service.response;

import com.ptit.service.entity.MultipleChoiceOption;
import com.ptit.service.entity.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponse {
    private Long id;
    private QuestionType type;
    private String content;
    private List<MultipleChoiceOption> options;
    private Double score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

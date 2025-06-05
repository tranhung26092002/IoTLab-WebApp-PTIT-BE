package com.ptit.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultipleChoiceOptionDTO {
    private Long id;
    private String option;
    private String content;

    @JsonIgnore
    private boolean isCorrect;
}
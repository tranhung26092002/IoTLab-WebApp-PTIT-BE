package com.ptit.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StudentAnswerListDTO {
    @NotEmpty(message = "Answers list cannot be empty")
    @Valid
    private List<StudentAnswerDTO> answers;
} 
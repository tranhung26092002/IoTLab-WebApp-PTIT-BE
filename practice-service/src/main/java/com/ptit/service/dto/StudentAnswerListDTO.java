package com.ptit.service.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class StudentAnswerListDTO {
    @NotEmpty(message = "Answers list cannot be empty")
    @Valid
    private List<StudentAnswerDTO> answers;
} 
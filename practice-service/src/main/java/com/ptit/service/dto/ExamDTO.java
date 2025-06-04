package com.ptit.service.dto;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private Long id;
    private String title;
    private String description;
    private List<ExamQuestionDTO> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
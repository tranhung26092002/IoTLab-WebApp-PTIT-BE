package com.ptit.service.response;

import com.ptit.service.entity.ExamQuestion;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private List<ExamQuestion> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

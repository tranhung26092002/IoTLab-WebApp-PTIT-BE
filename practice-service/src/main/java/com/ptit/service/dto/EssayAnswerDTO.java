package com.ptit.service.dto;

import lombok.Data;
import java.util.List;

@Data
public class EssayAnswerDTO {
    private Long id;
    private String answerText;
    private List<String> imageUrls;
}
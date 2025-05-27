package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportContentDTO {
    private Long id;
    private Long userId;
    private String content;
    private String performer;
    private String imageUrl;
    private Double evaluation;
}

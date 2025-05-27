package com.ptit.service.dto;

import com.ptit.service.entity.enums.PracticeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PracticeCreateDTO {
    private String title;
    private String description;
    private String imageUrl;
    private Integer practiceOrder;
    private PracticeStatus status;
}
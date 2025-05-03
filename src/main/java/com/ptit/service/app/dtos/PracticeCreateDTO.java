package com.ptit.service.app.dtos;

import com.ptit.service.domain.enums.PracticeStatus;
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
package com.ptit.service.dto;

import com.ptit.service.entity.enums.PracticeStatus;
import lombok.Data;

@Data
public class PraticeFilterDTO {
    private String id;
    private String title;
    private PracticeStatus status;

    private String sortField;
    private String sortOrder;
}

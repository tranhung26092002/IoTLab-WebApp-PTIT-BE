package com.ptit.service.app.dtos;

import com.ptit.service.domain.enums.PracticeStatus;
import lombok.Data;

@Data
public class PraticeFilterDTO {
    private String id;
    private String title;
    private PracticeStatus status;

    private String sortField;
    private String sortOrder;
}

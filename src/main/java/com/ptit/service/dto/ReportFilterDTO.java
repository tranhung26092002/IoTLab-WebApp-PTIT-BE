package com.ptit.service.dto;

import com.ptit.service.entity.enums.ReportStatus;
import com.ptit.service.entity.enums.ShiftType;
import lombok.Data;

@Data
public class ReportFilterDTO {
    private Long id;
    private Long userId;
    private String title;
    private String classGroup;
    private String className;
    private ShiftType shift;
    private ReportStatus status;
    private String startDate;
    private String endDate;

    private String sortField;
    private String sortOrder;
}

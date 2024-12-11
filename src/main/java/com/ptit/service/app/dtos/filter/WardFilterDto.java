package com.ptit.service.app.dtos.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WardFilterDto {
    private String codeWard;
    private String nameWard;
    private String codeDistrict;
    private String nameDistrict;
    private String codeProvince;
    private String nameProvince;

    private String sortField;
    private String sortOrder;
}

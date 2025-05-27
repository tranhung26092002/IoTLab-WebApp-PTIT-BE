package com.ptit.service.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String addressDetail;

    private String codeWard;

    private String codeDistrict;

    private String codeProvince;
}

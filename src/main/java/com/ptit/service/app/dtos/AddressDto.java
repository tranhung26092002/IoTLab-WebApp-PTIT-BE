package com.ptit.service.app.dtos;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data
public class AddressDto {
    private String addressDetail;

    private String codeWard;

    private String codeDistrict;

    private String codeProvince;
}

package com.ptit.service.domain.entities;

import lombok.*;

@Data
public class Address {
    private String addressDetail;

    private String codeWard;

    private String nameWard;

    private String codeDistrict;

    private String nameDistrict;

    private String codeProvince;

    private String nameProvince;
}

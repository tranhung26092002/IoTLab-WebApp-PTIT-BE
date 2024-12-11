package com.ptit.service.app.responses.address;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    private String addressDetail;
    private String codeWard;
    private String nameWard;
    private String codeDistrict;
    private String nameDistrict;
    private String codeProvince;
    private String nameProvince;
}
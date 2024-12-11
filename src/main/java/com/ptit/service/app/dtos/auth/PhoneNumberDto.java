package com.ptit.service.app.dtos.auth;


import com.ptit.service.domain.annotations.ValidPhoneNumber;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneNumberDto {

    @ValidPhoneNumber
    private String phoneNumber;
}

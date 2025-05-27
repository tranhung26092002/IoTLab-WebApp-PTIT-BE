package com.ptit.service.dto;


import com.ptit.service.annotation.ValidPhoneNumber;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneNumberDTO {

    @ValidPhoneNumber
    private String phoneNumber;
}

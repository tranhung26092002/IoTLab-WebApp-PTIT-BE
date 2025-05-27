package com.ptit.service.dto;

import com.ptit.service.entity.enums.Gender;
import com.ptit.service.entity.enums.RoleType;
import com.ptit.service.entity.enums.StateUser;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private String userName;

    private String fullName;

    private String phoneNumber;

    private String classCode;

    private String avatarUrl;

    private String email;

    private Gender gender;

    private LocalDate dateOfBirth;

    private AddressDto address;

    private RoleType roleType;

    private StateUser status;
}

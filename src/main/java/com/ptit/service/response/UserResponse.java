package com.ptit.service.response;

import com.ptit.service.entity.Address;
import com.ptit.service.entity.enums.Gender;
import com.ptit.service.entity.enums.RoleType;
import com.ptit.service.entity.enums.StateUser;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse extends BaseResponse {

    private Long id;

    private String userName;

    private String fullName;

    private String classCode;

    private String phoneNumber;

    private String email;

    private String avatarUrl;

    private Gender gender;

    private Address address;

    private LocalDate dateOfBirth;

    private StateUser status;

    private RoleType roleType;
}

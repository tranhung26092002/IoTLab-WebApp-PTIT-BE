package com.ptit.service.app.responses.user;

import com.ptit.service.domain.entities.Address;
import com.ptit.service.domain.enums.Gender;
import com.ptit.service.domain.enums.RoleType;
import com.ptit.service.domain.enums.StateUser;
import com.ptit.service.app.responses.address.AddressResponse;
import com.ptit.service.app.responses.BaseResponse;
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

    private String phoneNumber;

    private String email;

    private String avatarUrl;

    private Gender gender;

    private Address address;

    private LocalDate dateOfBirth;

    private StateUser status;

    private RoleType roleType;
}

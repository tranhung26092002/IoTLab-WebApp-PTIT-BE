package com.ptit.service.app.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ptit.service.domain.entities.Address;
import com.ptit.service.domain.enums.Gender;
import com.ptit.service.domain.enums.RoleType;
import com.ptit.service.domain.enums.StateUser;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private String userName;

    private String fullName;

    private String phoneNumber;

    private String email;

    private Gender gender;

    private LocalDate dateOfBirth;

    private AddressDto address;

    private RoleType roleType;

    private StateUser status;
}

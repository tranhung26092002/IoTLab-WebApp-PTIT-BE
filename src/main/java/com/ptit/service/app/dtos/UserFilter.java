package com.ptit.service.app.dtos;

import com.ptit.service.domain.enums.Gender;
import com.ptit.service.domain.enums.RoleType;
import lombok.Data;

@Data
public class UserFilter {
    private Long id;
    private String userName;
    private String fullName;
    private String classCode;
    private RoleType roleType;

    private String sortField;
    private String sortOrder;
}

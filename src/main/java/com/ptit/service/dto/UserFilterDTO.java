package com.ptit.service.dto;

import com.ptit.service.entity.enums.RoleType;
import lombok.Data;

@Data
public class UserFilterDTO {
    private Long id;
    private String userName;
    private String fullName;
    private String classCode;
    private RoleType roleType;

    private String sortField;
    private String sortOrder;
}

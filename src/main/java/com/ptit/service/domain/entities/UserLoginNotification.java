package com.ptit.service.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginNotification {
    private Long id;
    private String userName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String classCode;
}
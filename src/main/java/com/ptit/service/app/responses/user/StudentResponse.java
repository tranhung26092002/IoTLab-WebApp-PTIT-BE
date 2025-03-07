package com.ptit.service.app.responses.user;

import lombok.Data;

@Data
public class StudentResponse {
    private Long userId;
    private String name;
    private String studentCode;
}

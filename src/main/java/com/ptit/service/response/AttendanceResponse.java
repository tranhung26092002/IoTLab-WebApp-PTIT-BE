package com.ptit.service.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String fullName;
    private String classCode;
    private LocalDateTime checkInTime;
    private String shift;
}

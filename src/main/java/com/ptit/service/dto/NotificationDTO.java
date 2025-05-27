package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long userId;
    private Long borrowRecordId;
    private String title;
    private String message;
    private String expiredAt;
    private String type;
}
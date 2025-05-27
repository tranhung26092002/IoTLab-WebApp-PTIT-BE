package com.ptit.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private Long userId;
    private Long borrowRecordId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String type;
}

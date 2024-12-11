package com.ptit.service.app.responses;

import com.ptit.service.domain.enums.BorrowStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BorrowRecordResponse {
    private Long id;
    private DeviceReponse device;
    private Long userId;
    private String note;
    private LocalDate borrowedAt;
    private LocalDate expiredAt;
    private LocalDate returnedAt;
    private BorrowStatus status;
}

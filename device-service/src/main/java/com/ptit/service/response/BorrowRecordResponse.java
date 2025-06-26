package com.ptit.service.response;

import com.ptit.service.entity.enums.BorrowStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowRecordResponse {
    private Long id;
    private DeviceResponse device;
    private Long userId;
    private String note;
    private LocalDate borrowedAt;
    private LocalDate expiredAt;
    private LocalDate returnedAt;
    private BorrowStatus status;
}

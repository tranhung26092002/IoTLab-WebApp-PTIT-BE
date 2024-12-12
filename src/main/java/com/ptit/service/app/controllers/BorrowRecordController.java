package com.ptit.service.app.controllers;

import com.ptit.service.app.responses.BorrowRecordResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.BorrowRecord;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.services.BorrowRecordService;
import com.ptit.service.domain.utils.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/borrow-records")
@RequiredArgsConstructor
public class BorrowRecordController {
    private final BorrowRecordService borrowRecordService;

    @GetMapping("/history-of-user")
    public ResponsePage<BorrowRecordResponse> getBorrowHistoryOfUser(
            @RequestHeader(name = Constant.headerUserId) Long userId,
            Pageable pageable) {
        return borrowRecordService.getBorrowHistoryByUserId(userId, pageable);
    }

    @GetMapping("/devices-of-user")
    public ResponsePage<Device> getDevicesBorrowedByUser(
            @RequestHeader(name = Constant.headerUserId) Long userId,
            Pageable pageable
    ) {
        return borrowRecordService.getDevicesBorrowedByUser(userId, pageable);
    }

    @GetMapping("/history-of-device")
    public ResponsePage<BorrowRecord> getBorrowHistoryOfDevice(
            @RequestParam Long deviceId,
            Pageable pageable
    ) {
        return borrowRecordService.getBorrowHistoryByDeviceId(deviceId, pageable);
    }

    @GetMapping("/history")
    public ResponsePage<BorrowRecordResponse> getBorrowHistory(
            Pageable pageable
    ) {
        return borrowRecordService.getBorrowHistory(pageable);
    }

    @PostMapping("/borrow")
    public BorrowRecord borrowDevice(
            @RequestParam Long deviceId,
            @RequestHeader(name = Constant.headerUserId) Long userId,
            @RequestParam String note,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expiredAt
    ) {
        return borrowRecordService.createBorrowRecord(deviceId, userId, note, expiredAt);
    }

    @PostMapping("/return")
    public BorrowRecord returnDevice(
            @RequestParam Long deviceId,
            @RequestHeader(name = Constant.headerUserId) Long userId
    ) {
        return borrowRecordService.returnBorrowRecord(deviceId, userId);
    }
}

package com.ptit.service.controller;

import com.ptit.service.response.BorrowRecordResponse;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.entity.BorrowRecord;
import com.ptit.service.entity.Device;
import com.ptit.service.service.BorrowRecordService;
import com.ptit.service.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/borrow-records")
@RequiredArgsConstructor
public class BorrowRecordController extends BaseController {
    private final BorrowRecordService borrowRecordService;

    @GetMapping("/history-of-user")
    public ResponseEntity<DataResponse<PaginationData<BorrowRecordResponse>>> getBorrowHistoryOfUser(
            @RequestHeader(name = Constant.headerUserId) Long userId,
            Pageable pageable) {
        Page<BorrowRecordResponse> records = borrowRecordService.getBorrowHistoryByUserId(userId, pageable);
        return successWithPagination(records);
    }

    @GetMapping("/devices-of-user")
    public ResponseEntity<DataResponse<PaginationData<Device>>> getDevicesBorrowedByUser(
            @RequestHeader(name = Constant.headerUserId) Long userId,
            Pageable pageable
    ) {
        Page<Device> devices = borrowRecordService.getDevicesBorrowedByUser(userId, pageable);
        return successWithPagination(devices);
    }

    @GetMapping("/history-of-device")
    public ResponseEntity<DataResponse<PaginationData<BorrowRecord>>> getBorrowHistoryOfDevice(
            @RequestParam Long deviceId,
            Pageable pageable
    ) {
        Page<BorrowRecord> records = borrowRecordService.getBorrowHistoryByDeviceId(deviceId, pageable);
        return successWithPagination(records);
    }

    @GetMapping("/history")
    public ResponseEntity<DataResponse<PaginationData<BorrowRecordResponse>>> getBorrowHistory(
            Pageable pageable
    ) {
        Page<BorrowRecordResponse> records = borrowRecordService.getBorrowHistory(pageable);
        return successWithPagination(records);
    }

    @PostMapping("/borrow")
    public ResponseEntity<DataResponse<BorrowRecord>> borrowDevice(
            @RequestParam Long deviceId,
            @RequestHeader(name = Constant.headerUserId) Long userId,
            @RequestParam String note,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate expiredAt
    ) {
        BorrowRecord record = borrowRecordService.createBorrowRecord(deviceId, userId, note, expiredAt);
        return created(record);
    }

    @PostMapping("/return/{borrowRecordId}")
    public ResponseEntity<DataResponse<BorrowRecord>> returnDevice(
            @PathVariable Long borrowRecordId
    ) {
        Optional<BorrowRecord> record = borrowRecordService.returnBorrowRecord(borrowRecordId);
        if (record.isPresent()) {
            return success(record.get());
        } else {
            return ResponseEntity.ok(DataResponse.notFound("Borrow record not found"));
        }
    }
}

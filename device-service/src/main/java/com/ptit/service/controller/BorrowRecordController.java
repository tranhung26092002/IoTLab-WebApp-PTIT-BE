package com.ptit.service.controller;

import com.ptit.service.response.BorrowRecordResponse;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.entity.BorrowRecord;
import com.ptit.service.entity.Device;
import com.ptit.service.service.BorrowRecordService;
import com.ptit.service.util.Constant;
import io.swagger.annotations.*;
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
@Api(tags = "Borrow Record", description = "APIs quản lý mượn trả thiết bị")
public class BorrowRecordController extends BaseController {
    private final BorrowRecordService borrowRecordService;

    @GetMapping("/history-of-user")
    @ApiOperation(value = "Lấy lịch sử mượn của người dùng", notes = "Trả về lịch sử mượn thiết bị của người dùng hiện tại")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực")
    })
    public ResponseEntity<DataResponse<PaginationData<BorrowRecordResponse>>> getBorrowHistoryOfUser(
            @RequestHeader(name = Constant.headerUserId) Long userId,
            Pageable pageable) {
        Page<BorrowRecordResponse> records = borrowRecordService.getBorrowHistoryByUserId(userId, pageable);
        return successWithPagination(records);
    }

    @GetMapping("/devices-of-user")
    @ApiOperation(value = "Lấy thiết bị đang mượn của người dùng", notes = "Trả về danh sách thiết bị đang được người dùng mượn")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực")
    })
    public ResponseEntity<DataResponse<PaginationData<Device>>> getDevicesBorrowedByUser(
            @RequestHeader(name = Constant.headerUserId) Long userId,
            Pageable pageable
    ) {
        Page<Device> devices = borrowRecordService.getDevicesBorrowedByUser(userId, pageable);
        return successWithPagination(devices);
    }

    @GetMapping("/history-of-device")
    @ApiOperation(value = "Lấy lịch sử mượn của thiết bị", notes = "Trả về lịch sử mượn trả của một thiết bị cụ thể")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy thiết bị")
    })
    public ResponseEntity<DataResponse<PaginationData<BorrowRecord>>> getBorrowHistoryOfDevice(
            @RequestParam Long deviceId,
            Pageable pageable
    ) {
        Page<BorrowRecord> records = borrowRecordService.getBorrowHistoryByDeviceId(deviceId, pageable);
        return successWithPagination(records);
    }

    @GetMapping("/history")
    @ApiOperation(value = "Lấy lịch sử mượn trả tất cả", notes = "Trả về lịch sử mượn trả của tất cả thiết bị")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền truy cập")
    })
    public ResponseEntity<DataResponse<PaginationData<BorrowRecordResponse>>> getBorrowHistory(
            Pageable pageable
    ) {
        Page<BorrowRecordResponse> records = borrowRecordService.getBorrowHistory(pageable);
        return successWithPagination(records);
    }

    @PostMapping("/borrow")
    @ApiOperation(value = "Mượn thiết bị", notes = "Tạo bản ghi mượn thiết bị mới")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Mượn thành công"),
        @ApiResponse(code = 400, message = "Thiết bị không khả dụng hoặc dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa xác thực")
    })
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
    @ApiOperation(value = "Trả thiết bị", notes = "Cập nhật bản ghi mượn thành trả thiết bị")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Trả thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bản ghi mượn")
    })
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

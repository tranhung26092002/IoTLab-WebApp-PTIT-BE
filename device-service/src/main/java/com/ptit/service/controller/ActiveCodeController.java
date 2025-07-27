package com.ptit.service.controller;

import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.service.ActiveCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/admin/active-codes")
@Api(tags = "Active Code Management", description = "APIs quản lý active codes cho thiết bị IoT")
@Slf4j
public class ActiveCodeController extends BaseController {

    @Autowired
    private ActiveCodeService activeCodeService;

    @GetMapping
    @ApiOperation("Lấy danh sách tất cả active codes")
    public ResponseEntity<DataResponse<Set<String>>> getAllActiveCodes() {
        Set<String> activeCodes = activeCodeService.getAllActiveCodes();
        return success(activeCodes);
    }

    @PostMapping
    @ApiOperation("Thêm active code mới")
    public ResponseEntity<DataResponse<MessageResponse>> addActiveCode(@RequestParam String activeCode) {
        boolean added = activeCodeService.addActiveCode(activeCode);
        
        if (added) {
            return success(MessageResponse.builder()
                .message("Đã thêm active code thành công: " + activeCode)
                .build());
        } else {
            return ResponseEntity.badRequest()
                .body(DataResponse.badRequest("Active code đã tồn tại hoặc không hợp lệ"));
        }
    }

    @DeleteMapping
    @ApiOperation("Xóa active code")
    public ResponseEntity<DataResponse<MessageResponse>> removeActiveCode(@RequestParam String activeCode) {
        boolean removed = activeCodeService.removeActiveCode(activeCode);
        
        if (removed) {
            return success(MessageResponse.builder()
                .message("Đã xóa active code thành công: " + activeCode)
                .build());
        } else {
            return ResponseEntity.badRequest()
                .body(DataResponse.badRequest("Active code không tồn tại"));
        }
    }

    @PostMapping("/generate")
    @ApiOperation("Tạo active code mới")
    public ResponseEntity<DataResponse<MessageResponse>> generateActiveCode() {
        String newCode = activeCodeService.generateActiveCode();
        return success(MessageResponse.builder()
            .message("Đã tạo active code mới: " + newCode)
            .build());
    }

    @PostMapping("/validate")
    @ApiOperation("Kiểm tra active code có hợp lệ không")
    public ResponseEntity<DataResponse<MessageResponse>> validateActiveCode(@RequestParam String activeCode) {
        boolean isValid = activeCodeService.isValidActiveCode(activeCode);
        
        if (isValid) {
            return success(MessageResponse.builder()
                .message("Active code hợp lệ: " + activeCode)
                .build());
        } else {
            return ResponseEntity.badRequest()
                .body(DataResponse.badRequest("Active code không hợp lệ: " + activeCode));
        }
    }
} 
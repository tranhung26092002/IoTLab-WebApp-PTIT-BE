package com.ptit.service.controller;

import com.ptit.service.response.DeviceResponse;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.entity.Device;
import com.ptit.service.service.DeviceService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
@Api(tags = "Device Management", description = "APIs quản lý thiết bị IoT và thông tin thiết bị")
public class MQTTController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    @ApiOperation(value = "Lấy danh sách tất cả thiết bị", notes = "Trả về danh sách thiết bị IoT có phân trang")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền truy cập")
    })
    public ResponsePage<Device, DeviceResponse> getAllDevices(Pageable pageable) {
        return deviceService.getAllDevices(pageable);
    }

    @PostMapping
    @ApiOperation(value = "Lưu thiết bị mới", notes = "Tạo hoặc cập nhật thông tin thiết bị IoT")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Lưu thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền tạo")
    })
    public ResponseEntity<Device> saveDevice(@RequestBody Device device) {
        return ResponseEntity.ok(deviceService.saveDevice(device));
    }
}
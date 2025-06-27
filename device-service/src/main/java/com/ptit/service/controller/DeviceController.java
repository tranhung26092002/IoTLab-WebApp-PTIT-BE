package com.ptit.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ptit.service.dto.DeviceFilterDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.service.DeviceService;
import com.ptit.service.service.IotDeviceService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
@Api(tags = "Device Management", description = "APIs quản lý thiết bị IoT và thông tin thiết bị")
public class DeviceController extends BaseController {
    private final DeviceService deviceService;
    private final IotDeviceService iotDeviceService;

    @GetMapping("/{id}")
    @ApiOperation(value = "Lấy thông tin thiết bị theo ID", notes = "Trả về chi tiết thiết bị theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy thiết bị")
    })
    public ResponseEntity<DataResponse<Device>> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return success(device);
    }

    @GetMapping("/code")
    @ApiOperation(value = "Lấy thông tin thiết bị theo mã", notes = "Trả về chi tiết thiết bị theo mã thiết bị")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy thiết bị")
    })
    public ResponseEntity<DataResponse<Device>> getDeviceByCode(@RequestParam String code) {
        Device device = deviceService.getDeviceByCode(code);
        return success(device);
    }

    @GetMapping
    @ApiOperation(value = "Lấy danh sách tất cả thiết bị", notes = "Trả về danh sách thiết bị có phân trang")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền truy cập")
    })
    public ResponseEntity<DataResponse<PaginationData<Device>>> getAllDevices(Pageable pageable) {
        Page<Device> devices = deviceService.getAllDevices(pageable);
        return successWithPagination(devices);
    }

    @GetMapping("/regular")
    @ApiOperation(value = "Lấy danh sách thiết bị thường", notes = "Trả về danh sách thiết bị không phải IoT")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công")
    })
    public ResponseEntity<DataResponse<List<Device>>> getRegularDevices() {
        List<Device> regularDevices = deviceService.getDeviceRepository().findRegularDevices();
        return success(regularDevices);
    }

    @GetMapping("/iot")
    @ApiOperation(value = "Lấy danh sách thiết bị IoT", notes = "Trả về danh sách thiết bị IoT")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công")
    })
    public ResponseEntity<DataResponse<List<Device>>> getIotDevices() {
        List<Device> iotDevices = iotDeviceService.getAllIotDevices();
        return success(iotDevices);
    }

    @PostMapping
    @ApiOperation(value = "Tạo thiết bị mới", notes = "Tạo thiết bị mới với file đính kèm")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Tạo thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền tạo")
    })
    public ResponseEntity<DataResponse<Device>> createDevice(
            @RequestParam(value = "device", required = false) String deviceJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        // Chuyển đổi JSON thành đối tượng Device
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Đăng ký JavaTimeModule
        // objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Device device = null;

        if (deviceJson != null) {
            device = objectMapper.readValue(deviceJson, Device.class);
        }

        Device createdDevice = deviceService.createDevice(device, file);
        return created(createdDevice);
    }

    @GetMapping("/filter")
    @ApiOperation(value = "Lọc thiết bị", notes = "Lọc thiết bị theo các tiêu chí")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<DataResponse<PaginationData<Device>>> getDeviceFilter(
            @ModelAttribute DeviceFilterDTO deviceFilterDto,
            Pageable pageable
    ) {
        List<String> allowedFields = Arrays.asList(
                "id", "name", "type", "status");

        if (!allowedFields.contains(deviceFilterDto.getSortField())) {
            deviceFilterDto.setSortField("id");
        }
        Page<Device> devices = deviceService.getDeviceFilter(deviceFilterDto, pageable);
        return successWithPagination(devices);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Cập nhật thiết bị", notes = "Cập nhật thông tin thiết bị theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy thiết bị"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<DataResponse<Device>> updateDevice(
            @PathVariable Long id,
            @RequestParam(value = "device", required = false) String deviceJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        // Chuyển đổi JSON thành đối tượng Device
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Đăng ký JavaTimeModule
        // objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Device device = null;

        if (deviceJson != null) {
            device = objectMapper.readValue(deviceJson, Device.class);
        }
        Device updatedDevice = deviceService.updateDevice(id, device, file);

        return success(updatedDevice);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Xóa thiết bị", notes = "Xóa thiết bị theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Xóa thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy thiết bị")
    })
    public ResponseEntity<DataResponse<Void>> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return noContent();
    }
}

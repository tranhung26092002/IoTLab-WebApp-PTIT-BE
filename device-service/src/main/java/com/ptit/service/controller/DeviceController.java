package com.ptit.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ptit.service.dto.DeviceFilterDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.service.DeviceService;
import com.ptit.service.service.IotDeviceService;
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
public class DeviceController extends BaseController {
    private final DeviceService deviceService;
    private final IotDeviceService iotDeviceService;

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<Device>> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return success(device);
    }

    @GetMapping("/code")
    public ResponseEntity<DataResponse<Device>> getDeviceByCode(@RequestParam String code) {
        Device device = deviceService.getDeviceByCode(code);
        return success(device);
    }

    @GetMapping
    public ResponseEntity<DataResponse<PaginationData<Device>>> getAllDevices(Pageable pageable) {
        Page<Device> devices = deviceService.getAllDevices(pageable);
        return successWithPagination(devices);
    }

    @GetMapping("/regular")
    public ResponseEntity<DataResponse<List<Device>>> getRegularDevices() {
        List<Device> regularDevices = deviceService.getDeviceRepository().findRegularDevices();
        return success(regularDevices);
    }

    @GetMapping("/iot")
    public ResponseEntity<DataResponse<List<Device>>> getIotDevices() {
        List<Device> iotDevices = iotDeviceService.getAllIotDevices();
        return success(iotDevices);
    }

    @PostMapping
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
    public ResponseEntity<DataResponse<Void>> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return noContent();
    }
}

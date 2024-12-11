package com.ptit.service.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.app.dtos.DeviceFilterDto;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.services.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable Long id) {
        return deviceService.getDeviceById(id);
    }

    @GetMapping("/code")
    public Device getDeviceByCode(@RequestParam String code) {
        return deviceService.getDeviceByCode(code);
    }

    @GetMapping
    public ResponsePage<Device> getAllDevices(Pageable pageable) {
        return deviceService.getAllDevices(pageable);
    }

    @PostMapping
    public Device addDevice(@RequestBody Device device) {
        return deviceService.addDevice(device);
    }

    @GetMapping("/filter")
    public ResponsePage<Device> getJobPostFilter(
            @ModelAttribute DeviceFilterDto deviceFilterDto,
            Pageable pageable
    ){
        List<String> allowedFields = Arrays.asList(
                "id", "name", "type", "status");

        if (!allowedFields.contains(deviceFilterDto.getSortField())) {
            deviceFilterDto.setSortField("id");
        }
        return deviceService.getDeviceFilter(deviceFilterDto, pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(
            @PathVariable Long id,
            @RequestParam("device") String deviceJson,
            @RequestParam("file") MultipartFile file) throws IOException {
        // Chuyển đổi JSON thành đối tượng Device
        ObjectMapper objectMapper = new ObjectMapper();
        Device device = objectMapper.readValue(deviceJson, Device.class);

        Device updatedDevice = deviceService.updateDevice(id, device, file);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    public void deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
    }
}

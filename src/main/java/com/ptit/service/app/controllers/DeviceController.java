package com.ptit.service.app.controllers;

import com.ptit.service.app.responses.DeviceResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public ResponsePage<Device, DeviceResponse> getAllDevices(Pageable pageable) {
        return deviceService.getAllDevices(pageable);
    }

    @PostMapping
    public ResponseEntity<Device> saveDevice(@RequestBody Device device) {
        return ResponseEntity.ok(deviceService.saveDevice(device));
    }
}
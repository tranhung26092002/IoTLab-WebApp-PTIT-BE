package com.ptit.service.service;

import com.ptit.service.response.DeviceResponse;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.entity.Device;
import com.ptit.service.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    public ResponsePage<Device, DeviceResponse> getAllDevices(Pageable pageable) {
        Page<Device> devices = deviceRepository.findAll(pageable);

        return new ResponsePage<>(devices, DeviceResponse.class);
    }

    public Device saveDevice(Device device) {
        return deviceRepository.save(device);
    }

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public Device findByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId);
    }

    public Device findById(Long deviceId) {
        return deviceRepository.findById(deviceId).orElse(null);
    }
}
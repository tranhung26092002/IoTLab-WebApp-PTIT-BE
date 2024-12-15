package com.ptit.service.domain.services;

import com.ptit.service.app.responses.DeviceResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.repositories.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
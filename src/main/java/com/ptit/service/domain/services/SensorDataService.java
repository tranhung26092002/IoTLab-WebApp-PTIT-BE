package com.ptit.service.domain.services;

import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.SensorData;
import com.ptit.service.domain.repositories.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorDataService {
    @Autowired
    private SensorDataRepository sensorDataRepository;

    public SensorData saveSensorData(SensorData data) {
        return sensorDataRepository.save(data);
    }

    public void save(SensorData sensorData) {
        sensorDataRepository.save(sensorData);
    }

    public SensorData getLatestData(Long deviceId) {
        return sensorDataRepository.findFirstByDeviceIdOrderByCreatedAtDesc(deviceId);
    }

    public Page<SensorData> getHistory(Long deviceId, Pageable pageable) {
        // lấy 100 bản ghi gần nhất
        return sensorDataRepository.findTop100ByDeviceIdOrderByCreatedAtDesc(deviceId, pageable);
    }
}

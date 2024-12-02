package com.ptit.service.domain.services;

import com.ptit.service.app.controllers.WebSocketController;
import com.ptit.service.domain.entities.SensorData;
import com.ptit.service.domain.repositories.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;
    @Autowired
    private WebSocketController webSocketController;

    public List<SensorData> getSensorDataByNode(Long nodeId) {
        return sensorDataRepository.findAllByNodeId(nodeId); // Thêm điều kiện lọc nodeId nếu cần
    }

    public SensorData saveSensorData(SensorData sensorData) {
        // Lưu dữ liệu vào CSDL
        SensorData savedData = sensorDataRepository.save(sensorData);
//
//        // Gửi dữ liệu mới tới front-end qua WebSocket
//        webSocketController.broadcastSensorData(savedData);

        return savedData;
    }

    public SensorData getLatestSensorData() {
        return sensorDataRepository.findTopByOrderByIdDesc();
    }

    public SensorData getLatestDataForNode(Long nodeId) {
        // Get latest sensor data for specific node
        return sensorDataRepository.findTopByNodeIdOrderByTimestampDesc(nodeId)
            .orElseThrow(() -> new RuntimeException("No data found for node: " + nodeId));
    }

    public List<SensorData> getNodeHistory(Long nodeId) {
        // Get last 24 hours of data
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return sensorDataRepository.findByNodeIdAndTimestampAfterOrderByTimestampDesc(
            nodeId,
            twentyFourHoursAgo
        );
    }
}
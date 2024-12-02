package com.ptit.service.app.controllers;

import com.ptit.service.domain.entities.SensorData;
import com.ptit.service.domain.services.SensorDataService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor-data")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    @GetMapping("/node/{nodeId}")
    public List<SensorData> getSensorDataByNode(@PathVariable Long nodeId) {
        return sensorDataService.getSensorDataByNode(nodeId);
    }

    @GetMapping("/latest")
    public SensorData getLatestSensorData() {
        return sensorDataService.getLatestSensorData();
    }

    @PostMapping
    public SensorData saveSensorData(@RequestBody SensorData sensorData) {
        return sensorDataService.saveSensorData(sensorData);
    }
}

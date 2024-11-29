package com.ptit.service.app.controllers;

import com.ptit.service.domain.entities.SensorData;
import com.ptit.service.domain.services.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensor_data")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    @GetMapping
    public List<SensorData> getSensorDataByNode(@RequestParam Long nodeId) {
        return sensorDataService.getSensorDataByNode(nodeId);
    }

    @PostMapping
    public SensorData saveSensorData(@RequestBody SensorData sensorData) {
        return sensorDataService.saveSensorData(sensorData);
    }
}
